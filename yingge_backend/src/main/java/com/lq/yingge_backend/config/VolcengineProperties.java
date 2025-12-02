package com.lq.yingge_backend.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "volcengine.ark")
public class VolcengineProperties {

    private String apiKey;

    private String model;

    private String size;

    private boolean watermark;

    private Integer maxRetries;
}
