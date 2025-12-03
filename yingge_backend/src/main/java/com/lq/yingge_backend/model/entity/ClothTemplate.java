package com.lq.yingge_backend.model.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@TableName("cloth_template")
public class ClothTemplate {

    @TableId(type = IdType.AUTO)
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

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
