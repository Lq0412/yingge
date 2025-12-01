package com.lq.yingge_backend.service;

import com.lq.yingge_backend.model.dto.UserLoginRequest;
import com.lq.yingge_backend.model.dto.UserRegisterRequest;
import com.lq.yingge_backend.model.vo.UserVO;

public interface UserService {

    UserVO register(UserRegisterRequest request);

    UserVO login(UserLoginRequest request);
}

