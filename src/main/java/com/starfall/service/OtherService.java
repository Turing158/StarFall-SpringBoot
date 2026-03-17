package com.starfall.service;

import cn.hutool.captcha.GifCaptcha;
import com.starfall.dao.UserDao;
import com.starfall.entity.ResultMsg;
import com.starfall.entity.User;
import com.starfall.util.JwtUtil;
import com.starfall.util.RedisUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestHeader;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class OtherService {

    @Autowired
    RedisUtil redisUtil;

    public void getCodeImage(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        ServletOutputStream sos = resp.getOutputStream();
        GifCaptcha g = new GifCaptcha(100,40,4);
        g.createCode();
        String[] params = req.getQueryString().split("&");
        if(params.length > 1 && StringUtils.hasText(params[1])){
//            redisUtil.delete("code:"+params[1]);
        }
        redisUtil.set("code:"+params[0],g.getCode(),60);
        log.info("{}验证码：{}",params[0],g.getCode());
        g.write(sos);
    }


}
