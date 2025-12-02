package com.lq.yingge_backend.service.impl;

import com.lq.yingge_backend.manager.upload.FilePictureUpload;
import com.lq.yingge_backend.mapper.TryOnTaskMapper;
import com.lq.yingge_backend.model.entity.TryOnRecord;
import com.lq.yingge_backend.model.entity.TryOnTask;
import com.lq.yingge_backend.service.TryOnRecordService;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TryOnTaskAsyncExecutor {

    private static final int CONNECT_TIMEOUT_MS = 15_000;
    private static final int READ_TIMEOUT_MS = 60_000;
    private static final long MAX_RESULT_SIZE = 20L * 1024 * 1024; // 20MB 上限

    private final TryOnTaskMapper tryOnTaskMapper;
    private final VolcengineTryOnClient volcengineTryOnClient;
    private final FilePictureUpload filePictureUpload;
    private final TryOnRecordService tryOnRecordService;

    @Async("tryOnTaskExecutor")
    public void processTaskAsync(Long taskId) {
        TryOnTask task = tryOnTaskMapper.selectById(taskId);
        if (task == null) {
            return;
        }
        task.setStatus("processing");
        tryOnTaskMapper.updateById(task);
        try {
            String resultUrl = volcengineTryOnClient.compose(task.getPersonImageUrl(), task.getClothImageUrl(), task.getPrompt());
            String cosResultUrl = uploadResultToCos(task.getUserId(), resultUrl);
            task.setResultImageUrl(cosResultUrl);
            task.setStatus("done");
            task.setErrorMsg(null);
        } catch (Exception e) {
            log.error("处理试装任务失败, taskId={}, err={}", taskId, e.getMessage(), e);
            task.setStatus("failed");
            task.setErrorMsg(e.getMessage());
        }
        tryOnTaskMapper.updateById(task);
        writeRecord(task);
    }

    private void writeRecord(TryOnTask task) {
        TryOnRecord record = new TryOnRecord();
        record.setUserId(task.getUserId());
        record.setPersonImageUrl(task.getPersonImageUrl());
        record.setClothImageUrl(task.getClothImageUrl());
        record.setResultImageUrl(task.getResultImageUrl());
        record.setStatus(task.getStatus());
        record.setMessage(task.getErrorMsg());
        record.setIsDelete(0);
        tryOnRecordService.saveRecord(record);
    }

    private String uploadResultToCos(Long userId, String resultUrl) throws IOException {
        Path tempFile = Files.createTempFile("tryon-result-", ".png");
        try {
            downloadWithLimit(resultUrl, tempFile);
            String prefix = "user/" + userId + "/tryon/result";
            return filePictureUpload.uploadLocalFileToCos(tempFile.toString(), prefix);
        } finally {
            try {
                Files.deleteIfExists(tempFile);
            } catch (IOException e) {
                log.warn("删除临时文件失败: {}", e.getMessage());
            }
        }
    }

    private void downloadWithLimit(String url, Path target) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
        conn.setConnectTimeout(CONNECT_TIMEOUT_MS);
        conn.setReadTimeout(READ_TIMEOUT_MS);
        conn.setInstanceFollowRedirects(true);
        conn.connect();
        try (InputStream in = conn.getInputStream()) {
            byte[] buffer = new byte[8192];
            long total = 0;
            try (var out = Files.newOutputStream(target)) {
                int len;
                while ((len = in.read(buffer)) != -1) {
                    total += len;
                    if (total > MAX_RESULT_SIZE) {
                        throw new IOException("结果图超过大小限制");
                    }
                    out.write(buffer, 0, len);
                }
            }
        } finally {
            conn.disconnect();
        }
    }
}
