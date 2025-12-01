package com.lq.yingge_backend.controller;

import com.lq.yingge_backend.model.vo.StorageVO;
import com.lq.yingge_backend.model.vo.UserVO;
import com.lq.yingge_backend.service.StorageService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

@RestController
@RequestMapping("/api/storage")
@RequiredArgsConstructor
public class StorageController {

    private final StorageService storageService;

    @PostMapping("/upload")
    public ResponseEntity<StorageVO> upload(@RequestParam("file") MultipartFile file,
                                            @RequestParam("fileType") String fileType,
                                            HttpSession session) {
        UserVO loginUser = (UserVO) session.getAttribute("LOGIN_USER");
        if (loginUser == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "请先登录");
        }
        StorageVO vo = storageService.upload(loginUser.getId(), file, fileType);
        return ResponseEntity.ok(vo);
    }
}

