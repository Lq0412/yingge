package com.lq.yingge_backend.controller;

import com.lq.yingge_backend.model.dto.UserLoginRequest;
import com.lq.yingge_backend.model.dto.UserRegisterRequest;
import com.lq.yingge_backend.model.vo.UserVO;
import com.lq.yingge_backend.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "用户")
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    @Operation(summary = "用户注册", description = "传入账号/密码/确认密码，成功后自动登录并写入 session")
    public ResponseEntity<UserVO> register(@RequestBody UserRegisterRequest request, HttpSession session) {
        UserVO userVO = userService.register(request);
        session.setAttribute("LOGIN_USER", userVO);
        return ResponseEntity.ok(userVO);
    }

    @PostMapping("/login")
    @Operation(summary = "用户登录", description = "传入账号/密码，成功后写入 session")
    public ResponseEntity<UserVO> login(@RequestBody UserLoginRequest request, HttpSession session) {
        UserVO userVO = userService.login(request);
        session.setAttribute("LOGIN_USER", userVO);
        return ResponseEntity.ok(userVO);
    }
}
