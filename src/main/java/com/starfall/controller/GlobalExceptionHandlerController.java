package com.starfall.controller;

import com.starfall.Exception.NotLoginException;
import com.starfall.Exception.ServiceException;
import com.starfall.entity.ResultMsg;
import com.starfall.util.JsonOperate;
import com.starfall.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
public class GlobalExceptionHandlerController {

    @Autowired
    JwtUtil jwtUtil;

    @ExceptionHandler(NotLoginException.class)
    public void handleNotLoginException(NotLoginException e, HttpServletRequest request, HttpServletResponse response) throws IOException {
        jwtUtil.handleResponse(request,response).getWriter().write(JsonOperate.toJson(ResultMsg.error("NOT_LOGIN","未登录,请先登录")));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public void handleAccessDeniedException(AccessDeniedException e, HttpServletRequest request, HttpServletResponse response) throws IOException {
        jwtUtil.handleResponse(request,response).getWriter().write(JsonOperate.toJson(ResultMsg.error("ACCESS_ERROR","授权失败，请重试")));
    }

    @ExceptionHandler(AuthenticationException.class)
    public void handleAuthenticationException(AuthenticationException e, HttpServletRequest request, HttpServletResponse response) throws IOException {
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
        return ResultMsg.error("参数校验失败：" + String.join("; ", errors));
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResultMsg handleIllegalStateException(IllegalStateException ex) {
        return ResultMsg.error("参数校验失败：" + ex.getMessage());
    }

    @ExceptionHandler(ServiceException.class)
    public ResultMsg handleServiceException(ServiceException e) {
        return ResultMsg.error(e.getMsg());
    }

}
