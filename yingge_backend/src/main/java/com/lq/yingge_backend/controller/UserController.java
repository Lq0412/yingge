package com.lq.yingge_backend.controller;

import com.lq.yingge_backend.model.dto.UserLoginRequest;
import com.lq.yingge_backend.model.dto.UserRegisterRequest;
import com.lq.yingge_backend.model.vo.UserVO;
import com.lq.yingge_backend.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<UserVO> register(@RequestBody UserRegisterRequest request, HttpSession session) {
        UserVO userVO = userService.register(request);
        session.setAttribute("LOGIN_USER", userVO);
        return ResponseEntity.ok(userVO);
    }

    @PostMapping("/login")
    public ResponseEntity<UserVO> login(@RequestBody UserLoginRequest request, HttpSession session) {
        UserVO userVO = userService.login(request);
        session.setAttribute("LOGIN_USER", userVO);
        return ResponseEntity.ok(userVO);
    }
}

