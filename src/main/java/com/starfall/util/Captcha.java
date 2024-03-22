package com.starfall.util;
import cn.hutool.captcha.GifCaptcha;
import cn.hutool.core.img.gif.GifDecoder;
import jakarta.servlet.http.HttpSession;

public class Captcha {
    public void test(HttpSession session){
        GifCaptcha g = new GifCaptcha(80,40,4);
        g.createCode();
        session.setAttribute("code",g.getCode());
    }
}
