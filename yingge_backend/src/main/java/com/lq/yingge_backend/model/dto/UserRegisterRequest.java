package com.lq.yingge_backend.model.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRegisterRequest {

    private String userAccount;

    private String userPassword;

    private String checkPassword;

    private String userName;
}

