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

    @Value("${direct.access.url}")
    String[] directAccessUrl = {};

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse resp, FilterChain filterChain) throws ServletException, IOException {
//        解决options请求跨域不放行问题
        String origin = req.getHeader("Origin");
        resp.setHeader("Access-Control-Allow-Origin", origin);
        resp.setHeader("Access-Control-Allow-Credentials", "true");
        resp.setHeader("Access-Control-Allow-Headers", "x-requested-with,Authorization,token,Origin,Content-Type,Accept");
        if (HttpMethod.OPTIONS.toString().equals(req.getMethod())){
            log.info("OPTIONS请求，放行");
            resp.setStatus(HttpStatus.NO_CONTENT.value());
            return;
        }
        String url = req.getRequestURL().toString();
        for (int i = 0; i < directAccessUrl.length; i++) {
            if(url.contains(directAccessUrl[i])){
                log.info("公共操作::{}", url);
                filterChain.doFilter(req, resp);
                return;
            }
        }
        String token = req.getHeader("Authorization");
        if(token == null || token.isEmpty()){
            log.info("token为空，未登录，拒绝访问：{}",url);
            resp.setCharacterEncoding("UTF-8");
            resp.setContentType("application/json; charset=UTF-8");
            resp.getWriter().write(JsonOperate.toJson(ResultMsg.error("NOT_LOGIN","未登录,请先登录")));
            return;
        }
        else{
            try {
                JwtUtil.parseJWT(token);
            } catch (Exception e) {
                log.info("token解析失败，未登录,拒绝访问：{}",url);
                resp.setCharacterEncoding("UTF-8");
                resp.setContentType("application/json; charset=UTF-8");
                resp.getWriter().write(JsonOperate.toJson(ResultMsg.error("NOT_LOGIN","未登录,请先登录")));
                return;
            }
        }
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(token, null, null);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        filterChain.doFilter(req, resp);
    }
}
