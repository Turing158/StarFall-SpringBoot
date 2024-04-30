package com.starfall.service;

import cn.hutool.captcha.GifCaptcha;
import com.starfall.dao.UserDao;
import com.starfall.entity.ResultMsg;
import com.starfall.entity.User;
import com.starfall.util.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestHeader;

import java.awt.image.BufferedImage;
import java.io.IOException;

@Service
public class OtherService {

    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @Autowired
    UserDao userDao;

    public void getCodeImage(HttpSession session, HttpServletResponse resp) throws IOException {
        ServletOutputStream sos = resp.getOutputStream();
        GifCaptcha g = new GifCaptcha(100,40,4);
        g.createCode();
        session.setAttribute("code",g.getCode());
        g.write(sos);
    }

    public ResultMsg toAdmin( String token){

        Claims claims = JwtUtil.parseJWT(token);
        String role = (String) claims.get("ROLE");
        String user = (String) claims.get("USER");
        User userObj = userDao.findByUserOrEmail(user);
        if(!(role.equals("admin") || userObj.getRole().equals("admin"))){
            return ResultMsg.error("NOT_PERMISSION");
        }
        return ResultMsg.success();
    }
}
