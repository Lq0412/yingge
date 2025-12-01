package com.lq.yingge_backend.manager;

import org.springframework.web.multipart.MultipartFile;

public interface FileStorageManager {

    /**
     * 上传文件并返回可访问的 URL。
     */
    String upload(MultipartFile file, String targetPath);
}

