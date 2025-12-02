package com.lq.yingge_backend.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "cos.client")
public class CosProperties {

    /**
     * 访问域名，形如 https://bucket.cos.region.myqcloud.com
     */
    private String host;

    private String region;

    private String bucket;

    private String secretId;

    private String secretKey;
}
