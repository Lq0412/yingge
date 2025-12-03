package com.lq.yingge_backend.model.dto;

import java.math.BigDecimal;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClothTemplateRequest {

    private String name;

    private String imageUrl;

    private String maskUrl;

    private String category;

    /**
     * Style tags array, e.g. ["retro","wuxia"]
     */
    private List<String> styleTags;

    private String color;

    private String fit;

    private Integer status;

    private Integer sort;

    private String lang;

    private String prompt;

    private String negativePrompt;

    private BigDecimal strength;

    private String aspectRatio;
}
