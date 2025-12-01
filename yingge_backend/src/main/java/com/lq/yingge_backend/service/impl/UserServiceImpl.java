package com.lq.yingge_backend.service.impl;

import com.lq.yingge_backend.model.dto.UserLoginRequest;
import com.lq.yingge_backend.model.dto.UserRegisterRequest;
import com.lq.yingge_backend.model.entity.User;
import com.lq.yingge_backend.model.vo.UserVO;
import com.lq.yingge_backend.repository.UserRepository;
import com.lq.yingge_backend.service.UserService;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public UserVO register(UserRegisterRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("请求参数不能为空");
        }
        String userAccount = request.getUserAccount();
        String userPassword = request.getUserPassword();
        String checkPassword = request.getCheckPassword();
        if (!StringUtils.hasText(userAccount) || !StringUtils.hasText(userPassword) || !StringUtils.hasText(checkPassword)) {
            throw new IllegalArgumentException("账号或密码不能为空");
        }
        if (!Objects.equals(userPassword, checkPassword)) {
            throw new IllegalArgumentException("两次输入的密码不一致");
        }
        if (userPassword.length() < 6) {
            throw new IllegalArgumentException("密码长度不能少于6位");
        }
        boolean exists = userRepository.existsByUserAccountAndIsDelete(userAccount, 0);
        if (exists) {
            throw new IllegalArgumentException("账号已存在");
        }
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(passwordEncoder.encode(userPassword));
        user.setUserName(request.getUserName());
        user.setUserRole("user");
        user.setIsDelete(0);
        User saved = userRepository.save(user);
        return toUserVO(saved);
    }

    @Override
    public UserVO login(UserLoginRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("请求参数不能为空");
        }
        String userAccount = request.getUserAccount();
        String userPassword = request.getUserPassword();
        if (!StringUtils.hasText(userAccount) || !StringUtils.hasText(userPassword)) {
            throw new IllegalArgumentException("账号或密码不能为空");
        }
        User user = userRepository.findByUserAccountAndIsDelete(userAccount, 0)
                .orElseThrow(() -> new IllegalArgumentException("账号或密码错误"));
        if (!passwordEncoder.matches(userPassword, user.getUserPassword())) {
            throw new IllegalArgumentException("账号或密码错误");
        }
        return toUserVO(user);
    }

    private UserVO toUserVO(User user) {
        UserVO vo = new UserVO();
        vo.setId(user.getId());
        vo.setUserAccount(user.getUserAccount());
        vo.setUserName(user.getUserName());
        vo.setUserAvatar(user.getUserAvatar());
        vo.setUserRole(user.getUserRole());
        return vo;
    }
}

