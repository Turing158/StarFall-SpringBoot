package com.starfall.filter;

import com.starfall.entity.ResultMsg;
import com.starfall.util.JsonOperate;
import com.starfall.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

//Jwt认证过滤器
@Slf4j
@Component
public class JWTAuthenticationFilter extends OncePerRequestFilter {

    @Value("${direct.access.url}")
    String[] directAccessUrl = {};

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse resp, FilterChain filterChain) throws ServletException, IOException {

        String url = req.getRequestURL().toString();
        for (int i = 0; i < directAccessUrl.length; i++) {
            if(url.contains(directAccessUrl[i])){
                log.info("公共操作::{}", url);
                filterChain.doFilter(req, resp);
                return;
            }
        }
        String token = req.getHeader("token");
        if(token == null || token.isEmpty()){
            log.info("token为空，未登录");
            resp.setCharacterEncoding("UTF-8");
            resp.setContentType("application/json; charset=UTF-8");
            resp.getWriter().write(JsonOperate.toJson(ResultMsg.error("NOT_LOGIN","未登录,请先登录")));
        }
        else{
            try {
                JwtUtil.parseJWT(token);
            } catch (Exception e) {
                log.info("token解析失败，未登录");
                resp.setCharacterEncoding("UTF-8");
                resp.setContentType("application/json; charset=UTF-8");
                resp.getWriter().write(JsonOperate.toJson(ResultMsg.error("NOT_LOGIN","未登录,请先登录")));
            }
        }
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(token, null, null);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        filterChain.doFilter(req, resp);
    }
}
