package com.starfall.service;

import cn.hutool.captcha.GifCaptcha;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;

import java.awt.image.BufferedImage;
import java.io.IOException;

@Service
public class OtherService {


    public void getCodeImage(HttpSession session, HttpServletResponse resp) throws IOException {
        System.out.println(session.getId());
        ServletOutputStream sos = resp.getOutputStream();
        GifCaptcha g = new GifCaptcha(100,40,4);
        g.createCode();
        session.setAttribute("code",g.getCode());
        g.write(sos);
    }
}
