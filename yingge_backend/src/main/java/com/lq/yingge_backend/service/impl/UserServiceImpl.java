package com.lq.yingge_backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lq.yingge_backend.mapper.UserMapper;
import com.lq.yingge_backend.model.dto.UserLoginRequest;
import com.lq.yingge_backend.model.dto.UserRegisterRequest;
import com.lq.yingge_backend.model.entity.User;
import com.lq.yingge_backend.model.vo.UserVO;
import com.lq.yingge_backend.service.UserService;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;

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
        Long count = userMapper.selectCount(new LambdaQueryWrapper<User>()
                .eq(User::getUserAccount, userAccount)
                .eq(User::getIsDelete, 0));
        if (count != null && count > 0) {
            throw new IllegalArgumentException("账号已存在");
        }
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(passwordEncoder.encode(userPassword));
        user.setUserName(request.getUserName());
        user.setUserRole("user");
        user.setIsDelete(0);
        userMapper.insert(user);
        return toUserVO(user);
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
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>()
                .eq(User::getUserAccount, userAccount)
                .eq(User::getIsDelete, 0));
        if (user == null || !passwordEncoder.matches(userPassword, user.getUserPassword())) {
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
