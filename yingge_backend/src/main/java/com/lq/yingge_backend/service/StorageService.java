package com.lq.yingge_backend.service;

import com.lq.yingge_backend.model.vo.StorageVO;
import org.springframework.web.multipart.MultipartFile;

public interface StorageService {

    StorageVO upload(Long userId, MultipartFile file, String fileType);
}

