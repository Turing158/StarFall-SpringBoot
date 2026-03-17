package com.starfall.filter;

import com.starfall.Exception.NotLoginException;
import com.starfall.entity.ResultMsg;
import com.starfall.util.JsonOperate;
import com.starfall.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;



//Jwt认证过滤器
@Slf4j
@Component
public class JWTAuthenticationFilter extends OncePerRequestFilter {
    @Autowired
    JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse resp, FilterChain filterChain) throws ServletException, IOException {
//        解决options请求跨域不放行问题
        resp = jwtUtil.handleResponse(req,resp);
        if (HttpMethod.OPTIONS.toString().equals(req.getMethod())){
            log.info("OPTIONS请求，放行");
            resp.setStatus(HttpStatus.NO_CONTENT.value());
            return;
        }
        String url = req.getRequestURL().toString();
        String uri = req.getRequestURI();
        String[] directAccessUrl = jwtUtil.getDirectAccessUrl();
        for (int i = 0; i < directAccessUrl.length; i++) {
            if(uri.equals(directAccessUrl[i])
                    // 支持路径匹配
                    || (directAccessUrl[i].contains("/**") && uri.startsWith(directAccessUrl[i].replace("/**","")))){
                log.info("公共操作::{}", url);
                filterChain.doFilter(req, resp);
                return;
            }
        }
        String token = req.getHeader("Authorization");
        if(token == null || token.isEmpty()){
            log.info("token为空，未登录，拒绝访问：{}",url);
            throw new NotLoginException("NOT_LOGIN");
        }
        else{
            try {
                JwtUtil.parseJWT(token);
            } catch (Exception e) {
                log.info("token解析失败，未登录,拒绝访问：{}",url);
                throw new NotLoginException("NOT_LOGIN");
            }
        }
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(token, null, null);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        filterChain.doFilter(req, resp);
    }
}
