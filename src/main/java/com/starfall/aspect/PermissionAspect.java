package com.starfall.aspect;

import com.starfall.Exception.PermissionException;
import com.starfall.annotation.RequireRole;
import com.starfall.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.util.Arrays;

@Slf4j
@Aspect
@Component
public class PermissionAspect {
    @Autowired
    JwtUtil jwtUtil;

    @Pointcut("@annotation(com.starfall.annotation.RequireRole)")
    public void pointCut(){}
    @Before("pointCut()")
    public void checkRole(JoinPoint joinPoint) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        RequireRole methodAnnotation = method.getAnnotation(RequireRole.class);

        // 如果方法上没有注解，则获取类上的注解
        RequireRole annotation = methodAnnotation;
        if (annotation == null) {
            Class<?> targetClass = joinPoint.getTarget().getClass();
            annotation = AnnotationUtils.findAnnotation(targetClass, RequireRole.class);
        }
        if(annotation == null){
            return;
        }
        // 获取token
        String token = request.getHeader("Authorization");

        if (token == null || token.isEmpty()) {
            throw new PermissionException("TOKEN_NULL","未提供token");
        }
        if (!token.startsWith("Bearer ")) {
            throw new PermissionException("TOKEN_NULL","未提供token");
        }
        String role = jwtUtil.getTokenField(token,"ROLE");
        if(!Arrays.asList(annotation.value()).contains(role)){
            throw new PermissionException("PERMISSION_ERROR","权限不足");
        }
    }
}
