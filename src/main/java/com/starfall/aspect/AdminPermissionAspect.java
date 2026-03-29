package com.starfall.aspect;

import com.starfall.Exception.PermissionException;
import com.starfall.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Slf4j
@Aspect
@Component
public class AdminPermissionAspect {

    @Autowired
    JwtUtil jwtUtil;

    @Pointcut("execution(public * com.starfall.controller.admin..*.*(..))")
    public void pointCut(){}
    @Before("pointCut()")
    public void checkAdminRole(JoinPoint joinPoint) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();

        // 获取token
        String token = request.getHeader("Authorization");

        if (token == null || token.isEmpty()) {
            throw new PermissionException("TOKEN_NULL","未提供token");
        }
        if (!token.startsWith("Bearer ")) {
            throw new PermissionException("TOKEN_NULL","未提供token");
        }
        String role = jwtUtil.getTokenField(token,"ROLE");
        if (!"admin".equals(role)) {
            throw new PermissionException("PERMISSION_ERROR","权限不足");
        }
    }

}
