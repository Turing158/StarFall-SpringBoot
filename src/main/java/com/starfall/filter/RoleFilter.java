package com.starfall.filter;

import com.starfall.entity.ResultMsg;
import com.starfall.util.JsonOperate;
import com.starfall.util.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
@Component
public class RoleFilter extends OncePerRequestFilter {



    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse resp, FilterChain filterChain) throws ServletException, IOException {
        String url = req.getRequestURL().toString();
        String token = req.getHeader("Authorization");

        if(url.contains("/admin")){
            Claims claims = JwtUtil.parseJWT(token);
            String role = (String) claims.get("ROLE");
            if(!"admin".equals(role)){
                resp.setCharacterEncoding("UTF-8");
                resp.setContentType("application/json; charset=UTF-8");
                resp.getWriter().write(JsonOperate.toJson(ResultMsg.error("NOT_PERMISSION","权限不足")));
                return;
            }
            filterChain.doFilter(req, resp);
            return;
        }
        filterChain.doFilter(req, resp);
    }
}
