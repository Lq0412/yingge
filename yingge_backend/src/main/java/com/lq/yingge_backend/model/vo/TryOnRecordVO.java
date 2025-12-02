package com.lq.yingge_backend.model.vo;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TryOnRecordVO {

    private Long id;
    private String personImageUrl;
    private String clothImageUrl;
    private String resultImageUrl;
    private String status;
    private String message;
    private LocalDateTime createTime;
}
