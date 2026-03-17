package com.starfall.controller;

import com.starfall.Exception.AdminServiceException;
import com.starfall.Exception.NotLoginException;
import com.starfall.Exception.ParamException;
import com.starfall.Exception.ServiceException;
import com.starfall.entity.ResultMsg;
import com.starfall.util.JsonOperate;
import com.starfall.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.support.MethodArgumentNotValidException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandlerController {

    @Autowired
    JwtUtil jwtUtil;

    @ExceptionHandler(NotLoginException.class)
    public void handleNotLoginException(NotLoginException e, HttpServletRequest request, HttpServletResponse response) throws IOException {
        log.error("NotLoginException: {}", e.getMessage());
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        jwtUtil.handleResponse(request,response).getWriter().write(JsonOperate.toJson(ResultMsg.error("NOT_LOGIN","未登录,请先登录")));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public void handleAccessDeniedException(AccessDeniedException e, HttpServletRequest request, HttpServletResponse response) throws IOException {
        log.error("AccessDeniedException: {}", e.getMessage());
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        jwtUtil.handleResponse(request,response).getWriter().write(JsonOperate.toJson(ResultMsg.error("ACCESS_ERROR","授权失败，请重试")));
    }

    @ExceptionHandler(AuthenticationException.class)
    public void handleAuthenticationException(AuthenticationException e, HttpServletRequest request, HttpServletResponse response) throws IOException {
        log.error("AuthenticationException: {}", e.getMessage());
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        jwtUtil.handleResponse(request,response).getWriter().write(JsonOperate.toJson(ResultMsg.error("AUTHENTICATION_ERROR","认证失败，请重试")));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResultMsg handleValidationException(
            MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.toList());
        log.error("MethodArgumentNotValidException: {}", String.join("; ", errors));
        return ResultMsg.error("参数校验失败：" + String.join("; ", errors));
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResultMsg handleIllegalStateException(IllegalStateException ex) {
        log.error("IllegalStateException: {}", ex.getMessage());
        return ResultMsg.error("参数校验失败：" + ex.getMessage());
    }

    @ExceptionHandler(ServiceException.class)
    public ResultMsg handleServiceException(ServiceException e) {
        log.error("ServiceException: {}", e.getMessage());
        return ResultMsg.error(e.getMsg());
    }

    @ExceptionHandler(AdminServiceException.class)
    public ResultMsg handleAdminServiceException(AdminServiceException e) {
        log.error("AdminServiceException: {}", e.getMessage());
        return ResultMsg.error(e.getMsg());
    }

    @ExceptionHandler(ParamException.class)
    public ResultMsg handleParamException(ParamException e) {
        log.error("ParamException: {}", e.getMessage());
        return ResultMsg.error(e.getMsg());
    }
}
