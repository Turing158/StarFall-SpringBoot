package com.starfall.config;


import com.starfall.entity.ResultMsg;
import com.starfall.util.JsonOperate;
import com.starfall.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@Slf4j
public class InterceptorFilter implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest req, HttpServletResponse resp, Object handler) throws Exception {
        if (HttpMethod.OPTIONS.toString().equals(req.getMethod())){
            resp.setStatus(HttpStatus.NO_CONTENT.value());
            return true;
        }
        String url = req.getRequestURL().toString();
        if(
                url.contains("login")
                || url.contains("register")
                || url.contains("getCodeImage")
                || url.contains("getEmailCode")
                || url.contains("findUserByUser")
                || url.contains("findAllNotice")
                || url.contains("findAllTopic")
                || url.contains("getTopicInfo")
                || url.contains("findAllTopicByUser")
                || url.contains("findTopicVersion")
                || url.contains("findCommentByTopic")
        ){
            log.info("公共操作::{}", url);
            return true;
        }
        String token = req.getHeader("token");
        if(token == null || token.isEmpty()){
            log.info("token为空，未登录");
            resp.getWriter().write(JsonOperate.toJson(ResultMsg.error("NOT_LOGIN")));
            return false;
        }
        try {
            JwtUtil.parseJWT(token);
        } catch (Exception e) {
            log.info("token解析失败，未登录");
            resp.getWriter().write(JsonOperate.toJson(ResultMsg.error("NOT_LOGIN")));
            return false;
        }
        return true;
    }
}
