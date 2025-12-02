package com.lq.yingge_backend.model.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@TableName("try_on_record")
public class TryOnRecord {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private String personImageUrl;

    private String clothImageUrl;

    private String resultImageUrl;

    private String status;

    private String message;

    private Integer isDelete;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
