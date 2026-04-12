package com.starfall.filter;

import com.starfall.Exception.NotLoginException;
import com.starfall.controller.GlobalExceptionHandlerController;
import com.starfall.util.EncDecUtil;
import com.starfall.util.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
    @Autowired
    EncDecUtil encDecUtil;
    @Autowired
    GlobalExceptionHandlerController globalExceptionHandlerController;

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse resp, FilterChain filterChain) throws ServletException, IOException {
//        解决options请求跨域不放行问题
        resp = jwtUtil.handleResponse(req,resp);
        if (HttpMethod.OPTIONS.toString().equals(req.getMethod())){
//            log.info("OPTIONS请求，放行");
            resp.setStatus(HttpStatus.NO_CONTENT.value());
            return;
        }
        String url = req.getRequestURL().toString();
        String uri = req.getRequestURI();
        String[] directAccessUrl = jwtUtil.getDirectAccessUrl();
        for (String s : directAccessUrl) {
            if (uri.equals(s)
                    // 支持路径匹配
                    || (s.contains("/**") && uri.startsWith(s.replace("/**", "")))) {
//                log.info("公共操作:{}", url);
                filterChain.doFilter(req, resp);
                return;
            }
        }
        String token = req.getHeader("Authorization");
        Claims claims;
        if(token == null || !token.startsWith("Bearer ")){
            log.info("【JWT认证Filter】Authorization header为空，未登录，拒绝访问：{}",url);
            globalExceptionHandlerController.handleNotLoginException(new NotLoginException("NOT_LOGIN"),req,resp);
            return;
        }
        try {
            claims = jwtUtil.parseToken(token);
        } catch (Exception e) {
            log.info("【JWT认证Filter】token解析失败，未登录,拒绝访问：{}", url);
            globalExceptionHandlerController.handleNotLoginException(new NotLoginException("NOT_LOGIN"), req, resp);
            return;
        }
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(token, null, null);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        log.info("【JWT认证Filter】用户：{} 角色：{} 访问 :{}",claims.get("USER"),claims.get("ROLE"),url);
        filterChain.doFilter(req, resp);
    }


}
