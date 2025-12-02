package com.lq.yingge_backend.manager.upload;

import com.lq.yingge_backend.manager.CosManager;
import jakarta.annotation.Resource;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
import javax.imageio.ImageIO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Component
public class FilePictureUpload {

    @Resource
    private CosManager cosManager;

    /**
     * 上传结果（包含本地路径和 COS URL）
     */
    public static class UploadResult {
        private final String localPath;
        private final String cosUrl;

        public UploadResult(String localPath, String cosUrl) {
            this.localPath = localPath;
            this.cosUrl = cosUrl;
        }

        public String getLocalPath() {
            return localPath;
        }

        public String getCosUrl() {
            return cosUrl;
        }
    }

    /**
     * 上传图片到本地与 COS。
     * 会将图片转换为 jpeg/png 以保证兼容性。
     */
    public UploadResult uploadPictureToCosAndLocal(MultipartFile file, String uploadPathPrefix, Path localSaveDir) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("文件为空");
        }
        try {
            Files.createDirectories(localSaveDir);
            BufferedImage image;
            try (InputStream inputStream = file.getInputStream()) {
                image = ImageIO.read(inputStream);
            }
            if (image == null) {
                throw new IllegalArgumentException("不支持的图片格式，请转换为 jpeg/png");
            }

            String targetFormat = determineTargetFormat(file.getOriginalFilename(), image);
            String targetExt = targetFormat.equals("jpeg") ? "jpg" : "png";
            String fileName = UUID.randomUUID().toString().replace("-", "") + "." + targetExt;

            Path localPath = localSaveDir.resolve(fileName);
            boolean written = ImageIO.write(image, targetFormat, localPath.toFile());
            if (!written) {
                throw new IOException("写入图片失败");
            }

            String key = uploadPathPrefix + "/" + fileName;
            String cosUrl = cosManager.putObject(key, localPath.toFile());
            log.info("图片已上传到 COS: key={}, url={}", key, cosUrl);
            return new UploadResult(localPath.toAbsolutePath().toString(), cosUrl);
        } catch (IOException e) {
            throw new IllegalStateException("图片保存失败: " + e.getMessage(), e);
        }
    }

    /**
     * 上传本地文件到 COS。
     */
    public String uploadLocalFileToCos(String localFilePath, String uploadPathPrefix) {
        File file = new File(localFilePath);
        if (!file.exists()) {
            throw new IllegalArgumentException("文件不存在: " + localFilePath);
        }
        String key = uploadPathPrefix + "/" + file.getName();
        return cosManager.putObject(key, file);
    }

    private String determineTargetFormat(String originalExt, BufferedImage image) {
        String ext = getExtension(originalExt);
        if ("png".equalsIgnoreCase(ext)) {
            return "png";
        }
        // 有 alpha 通道也用 png，否则默认 jpeg
        return image.getColorModel().hasAlpha() ? "png" : "jpeg";
    }

    private String getExtension(String filename) {
        if (filename == null || filename.isEmpty()) {
            return null;
        }
        int idx = filename.lastIndexOf('.');
        if (idx >= 0 && idx < filename.length() - 1) {
            return filename.substring(idx + 1).toLowerCase();
        }
        return null;
    }
}
