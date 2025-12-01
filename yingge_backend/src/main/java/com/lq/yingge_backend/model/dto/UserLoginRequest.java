package com.lq.yingge_backend.model.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserLoginRequest {

    private String userAccount;

    private String userPassword;
}

