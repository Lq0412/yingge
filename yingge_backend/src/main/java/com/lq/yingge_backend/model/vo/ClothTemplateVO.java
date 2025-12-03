package com.lq.yingge_backend.model.vo;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClothTemplateVO {

    private Long id;

    private String name;

    private String imageUrl;

    private String maskUrl;

    private String category;

    private String styleTags;

    private String color;

    private String fit;

    private Integer status;

    private Integer sort;

    private String lang;

    private String prompt;

    private String negativePrompt;

    private BigDecimal strength;

    private String aspectRatio;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
