package com.lq.yingge_backend.service.impl;

import com.lq.yingge_backend.config.VolcengineProperties;
import com.volcengine.ark.runtime.model.images.generation.GenerateImagesRequest;
import com.volcengine.ark.runtime.model.images.generation.ImagesResponse;
import com.volcengine.ark.runtime.model.images.generation.ResponseFormat;
import com.volcengine.ark.runtime.service.ArkService;
import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Slf4j
@Component
public class VolcengineTryOnClient {

    private final ArkService arkService;
    private final VolcengineProperties properties;

    public VolcengineTryOnClient(VolcengineProperties properties) {
        this.properties = properties;
        if (!StringUtils.hasText(properties.getApiKey())) {
            throw new IllegalStateException("Volcengine apiKey 未配置");
        }
        this.arkService = ArkService.builder()
                .apiKey(properties.getApiKey())
                .build();
    }

    /**
     * 调用火山 Ark 试装，返回结果 URL。
     */
    public String compose(String personImageUrl, String clothImageUrl, String prompt) {
        if (!StringUtils.hasText(personImageUrl) || !StringUtils.hasText(clothImageUrl)) {
            throw new IllegalArgumentException("图片 URL 不能为空");
        }
        if (!StringUtils.hasText(properties.getModel())) {
            throw new IllegalStateException("Volcengine 模型未配置");
        }
        try {
            GenerateImagesRequest request = GenerateImagesRequest.builder()
                    .model(properties.getModel())
                    .image(Arrays.asList(personImageUrl, clothImageUrl))
                    .size(properties.getSize())
                    .sequentialImageGeneration("disabled")
                    .responseFormat(ResponseFormat.Url)
                    .stream(false)
                    .watermark(properties.isWatermark())
                    .prompt(StringUtils.hasText(prompt) ? prompt.trim() : null)
                    .build();
            ImagesResponse response = arkService.generateImages(request);
            if (response == null || response.getData() == null || response.getData().isEmpty()) {
                throw new IllegalStateException("Volcengine 返回结果为空");
            }
            String resultUrl = response.getData().get(0).getUrl();
            if (!StringUtils.hasText(resultUrl)) {
                throw new IllegalStateException("Volcengine 返回空结果 URL");
            }
            return resultUrl;
        } catch (Exception e) {
            log.error("Volcengine 调用失败: {}", e.getMessage(), e);
            throw new IllegalStateException("Volcengine 调用失败: " + e.getMessage(), e);
        }
    }
}
