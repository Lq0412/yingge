package com.lq.yingge_backend.controller;

import com.lq.yingge_backend.annotation.LoginRequired;
import com.lq.yingge_backend.manager.upload.FilePictureUpload;
import com.lq.yingge_backend.model.vo.UserVO;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/storage")
@RequiredArgsConstructor
@Tag(name = "存储上传")
public class StorageController {

    private static final long MAX_SIZE = 5 * 1024 * 1024;

    private final FilePictureUpload filePictureUpload;

    @LoginRequired
    @PostMapping("/upload")
    @Operation(summary = "上传图片到 COS", description = "校验 jpg/png 且 ≤5MB，返回 COS URL")
    public ResponseEntity<String> upload(@RequestParam("file") MultipartFile file,
                                         @RequestParam(value = "type", defaultValue = "misc") String type,
                                         HttpSession session) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("文件不能为空");
        }
        if (file.getSize() > MAX_SIZE) {
            throw new IllegalArgumentException("文件超过 5MB 限制");
        }
        UserVO loginUser = (UserVO) session.getAttribute("LOGIN_USER");
        String prefix = "user/" + loginUser.getId() + "/" + type;
        FilePictureUpload.UploadResult result = filePictureUpload.uploadPictureToCosAndLocal(file, prefix,
                java.nio.file.Paths.get("uploads").toAbsolutePath());
        return ResponseEntity.ok(result.getCosUrl());
    }
}
