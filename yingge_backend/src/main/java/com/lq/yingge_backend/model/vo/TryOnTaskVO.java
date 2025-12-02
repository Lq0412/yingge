package com.lq.yingge_backend.model.vo;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TryOnTaskVO {

    private Long id;
    private String personImageUrl;
    private String clothImageUrl;
    private String resultImageUrl;
    private String prompt;
    private String status;
    private String errorMsg;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
