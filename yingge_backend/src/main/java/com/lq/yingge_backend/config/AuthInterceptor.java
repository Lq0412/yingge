package com.lq.yingge_backend.config;

import com.lq.yingge_backend.annotation.AdminRequired;
import com.lq.yingge_backend.annotation.LoginRequired;
import com.lq.yingge_backend.model.vo.UserVO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod handlerMethod)) {
            return true;
        }
        boolean needLogin = handlerMethod.hasMethodAnnotation(LoginRequired.class)
                || handlerMethod.getBeanType().isAnnotationPresent(LoginRequired.class)
                || handlerMethod.hasMethodAnnotation(AdminRequired.class)
                || handlerMethod.getBeanType().isAnnotationPresent(AdminRequired.class);
        if (!needLogin) {
            return true;
        }
        HttpSession session = request.getSession(false);
        UserVO user = session == null ? null : (UserVO) session.getAttribute("LOGIN_USER");
        if (user == null) {
            writeError(response, HttpStatus.UNAUTHORIZED, "请先登录");
            return false;
        }
        boolean needAdmin = handlerMethod.hasMethodAnnotation(AdminRequired.class)
                || handlerMethod.getBeanType().isAnnotationPresent(AdminRequired.class);
        if (needAdmin && !"admin".equalsIgnoreCase(user.getUserRole())) {
            writeError(response, HttpStatus.FORBIDDEN, "需要管理员权限");
            return false;
        }
        return true;
    }

    private void writeError(HttpServletResponse response, HttpStatus status, String message) {
        response.setStatus(status.value());
        response.setContentType("application/json;charset=UTF-8");
        try {
            response.getWriter().write("{\"code\":" + status.value() + ",\"message\":\"" + message + "\"}");
        } catch (IOException e) {
            throw new IllegalStateException("写入鉴权错误响应失败", e);
        }
    }
}
