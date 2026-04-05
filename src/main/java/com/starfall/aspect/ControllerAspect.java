package com.starfall.aspect;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Aspect
@Component
public class ControllerAspect {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Pointcut("execution(public * com.starfall.controller..*.*(..))")
    public void controllerLog() {
    }
    @Before("controllerLog()")
    public void doBefore(JoinPoint joinPoint) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        request.setAttribute("startTime", System.currentTimeMillis());
        if(joinPoint.getSignature().getName().equals("handleServiceException")){
            return;
        }
        log.info("【发起请求】{} {}\tIP: {}\t方法: {}.{}\t参数: {}",
                request.getMethod(),
                request.getRequestURL(),
                getIpAddress(request),
                joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName(),
                getRequestParams(joinPoint.getArgs())
        );
    }

    @AfterReturning(pointcut = "controllerLog()", returning = "result")
    public void doAfterReturning(JoinPoint joinPoint, Object result) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        Long startTime = (Long) request.getAttribute("startTime");
        long duration = startTime != null ? System.currentTimeMillis() - startTime : 0;
        log.info("【请求结束】{} {}\tIP: {}\t方法: {}.{}\t耗时: {}\t响应: {}",
                request.getMethod(),
                request.getRequestURL(),
                getIpAddress(request),
                joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName(),
                startTime != null ? parseDuration(duration) : "~ms",
                getResponseData(result));
    }

    @AfterThrowing(pointcut = "controllerLog()", throwing = "ex")
    public void doAfterThrowing(JoinPoint joinPoint, Exception ex) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        log.error("【请求异常】{} {}\tIP: {}\t方法: {}.{}\t异常: {}\t栈: {}",
                request.getMethod(),
                request.getRequestURL(),
                getIpAddress(request),
                joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName(),
                ex.getMessage(),
                ex.getStackTrace()
        );
    }


//    获取请求参数（处理文件等特殊类型）
    private String getRequestParams(Object[] args) {
        if (args == null || args.length == 0) {
            return "null";
        }

        List<Object> paramList = Arrays.stream(args)
                .filter(arg -> !(arg instanceof HttpServletRequest)
                        && !(arg instanceof HttpServletResponse)
                        && !(arg instanceof MultipartFile))
                .collect(Collectors.toList());

        if (paramList.isEmpty()) {
            return "[]";
        }

        try {
            return objectMapper.writeValueAsString(paramList);
        } catch (JsonProcessingException e) {
            return paramList.toString();
        }
    }

//    获取响应数据（限制长度，防止日志过大）
    private String getResponseData(Object result) {
        if (result == null) {
            return "null";
        }

        try {
            String responseStr = objectMapper.writeValueAsString(result);
            if (responseStr.length() > 2000) {
                return responseStr.substring(0, 2000) + "... [truncated]";
            }
            return responseStr;
        } catch (JsonProcessingException e) {
            return result.toString();
        }
    }

//    获取真实 IP 地址（考虑代理情况）
    private String getIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // 多级代理的情况，取第一个 IP
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }

    private String parseDuration(long duration) {
        if (duration < 1000) {
            return duration + "ms";
        } else if (duration < 60 * 1000) {
            return String.format("%.2f秒", duration / 1000.0);
        } else {
            return String.format("%.2f分钟", duration / (60 * 1000.0));
        }
    }
}
