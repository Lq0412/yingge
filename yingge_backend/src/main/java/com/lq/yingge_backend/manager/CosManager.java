package com.lq.yingge_backend.manager;

import com.lq.yingge_backend.config.CosProperties;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.exception.CosClientException;
import com.qcloud.cos.model.PutObjectRequest;
import java.io.File;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CosManager {

    private final COSClient cosClient;
    private final CosProperties cosProperties;

    /**
     * 上传文件到 COS，并返回可访问 URL。
     */
    public String putObject(String key, File file) {
        try {
            PutObjectRequest request = new PutObjectRequest(cosProperties.getBucket(), key, file);
            cosClient.putObject(request);
            return buildUrl(key);
        } catch (CosClientException e) {
            throw new IllegalStateException("上传文件到 COS 失败: " + e.getMessage(), e);
        }
    }

    private String buildUrl(String key) {
        String host = cosProperties.getHost();
        String cleanHost = host != null ? host.replaceAll("/+$", "") : null;
        String cleanKey = key.startsWith("/") ? key.substring(1) : key;
        if (cleanHost != null && !cleanHost.isBlank()) {
            return cleanHost + "/" + cleanKey;
        }
        // 默认拼接 COS 域名
        return "https://" + cosProperties.getBucket() + ".cos." + cosProperties.getRegion() + ".myqcloud.com/" + cleanKey;
    }
}
