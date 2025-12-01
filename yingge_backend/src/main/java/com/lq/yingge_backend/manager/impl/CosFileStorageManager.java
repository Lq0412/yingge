package com.lq.yingge_backend.manager.impl;

import com.lq.yingge_backend.config.CosProperties;
import com.lq.yingge_backend.manager.FileStorageManager;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.exception.CosClientException;
import com.qcloud.cos.exception.CosServiceException;
import com.qcloud.cos.model.ObjectMetadata;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.model.GeneratePresignedUrlRequest;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
@RequiredArgsConstructor
public class CosFileStorageManager implements FileStorageManager {

    private final COSClient cosClient;
    private final CosProperties cosProperties;

    @Override
    public String upload(MultipartFile file, String targetPath) {
        String originalFilename = file.getOriginalFilename();
        String key = buildObjectKey(targetPath, originalFilename);
        try (InputStream inputStream = file.getInputStream()) {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(file.getSize());
            String contentType = file.getContentType();
            if (contentType != null && !contentType.isBlank()) {
                metadata.setContentType(contentType);
            }
            PutObjectRequest request = new PutObjectRequest(cosProperties.getBucket(), key, inputStream, metadata);
            cosClient.putObject(request);
            return buildFileUrl(key);
        } catch (CosClientException e) {
            throw new IllegalStateException("上传文件到 COS 失败", e);
        } catch (IOException e) {
            throw new IllegalStateException("读取上传文件失败", e);
        }
    }

    private String buildObjectKey(String targetPath, String originalFilename) {
        String cleanName = originalFilename == null ? "file" : originalFilename.replaceAll("\\s+", "");
        return targetPath + "/" + UUID.randomUUID() + "-" + cleanName;
    }

    private String buildFileUrl(String key) {
        String host = cosProperties.getHost();
        if (host != null && !host.isBlank()) {
            String cleanHost = host.endsWith("/") ? host.substring(0, host.length() - 1) : host;
            String cleanKey = key.startsWith("/") ? key.substring(1) : key;
            return cleanHost + "/" + cleanKey;
        }
        // 无自定义 host 则生成带有效期的预签名 URL（默认 24h）
        try {
            GeneratePresignedUrlRequest req = new GeneratePresignedUrlRequest(cosProperties.getBucket(), key);
            long oneDayMillis = 24 * 60 * 60 * 1000L;
            req.setExpiration(new java.util.Date(System.currentTimeMillis() + oneDayMillis));
            URL url = cosClient.generatePresignedUrl(req);
            return url.toString();
        } catch (Exception e) {
            throw new IllegalStateException("生成文件访问 URL 失败", e);
        }
    }
}
