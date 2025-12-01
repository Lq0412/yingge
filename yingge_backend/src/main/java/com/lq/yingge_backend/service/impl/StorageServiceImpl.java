package com.lq.yingge_backend.service.impl;

import com.lq.yingge_backend.manager.FileStorageManager;
import com.lq.yingge_backend.model.entity.Storage;
import com.lq.yingge_backend.model.vo.StorageVO;
import com.lq.yingge_backend.repository.StorageRepository;
import com.lq.yingge_backend.service.StorageService;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class StorageServiceImpl implements StorageService {

    private final StorageRepository storageRepository;
    private final FileStorageManager fileStorageManager;

    @Override
    public StorageVO upload(Long userId, MultipartFile file, String fileType) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("文件不能为空");
        }
        if (!StringUtils.hasText(fileType)) {
            throw new IllegalArgumentException("文件类型不能为空");
        }
        // 按用户区分目录，便于后续管理
        String targetPath = "user/" + userId + "/" + fileType;
        String fileUrl = fileStorageManager.upload(file, targetPath);
        Storage storage = new Storage();
        storage.setFileName(file.getOriginalFilename());
        storage.setFilePath(fileUrl);
        storage.setFileType(fileType);
        storage.setUploadTime(LocalDateTime.now());
        Storage saved = storageRepository.save(storage);
        StorageVO vo = new StorageVO();
        vo.setStorageId(saved.getStorageId());
        vo.setFileName(saved.getFileName());
        vo.setFileType(saved.getFileType());
        vo.setFileUrl(saved.getFilePath());
        return vo;
    }
}
