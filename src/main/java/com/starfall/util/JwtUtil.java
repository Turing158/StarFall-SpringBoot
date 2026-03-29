package com.starfall.util;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;

@Component
@RefreshScope
//Jwt工具类，提供了生成和解析JWT的功能，同时还包含了获取直接访问URL的方法
public class JwtUtil {
    @Value("${jwt.key}")
    private static String key = "StarFall";
    @Value("${jwt.expire}")
    private Long expire = 24*60*60L + 1;

    @Value("${direct.url.user}")
    private String[] userDirectAccessUrl = { };
    @Value("${direct.url.topic}")
    private String[] topicDirectAccessUrl = { };
    @Value("${direct.url.home}")
    private String[] homeDirectAccessUrl = { };
    @Value("${direct.url.other}")
    private String[] otherDirectAccessUrl = { };
    @Autowired
    EncDecUtil encDecUtil;

//    生成加密token字符串（expireAfterSeconds单位：秒）
    public String generateToken(Map<String, Object> claims, long expireAfterSeconds){
        String jwt = Jwts.builder()
                .addClaims(claims)
                .signWith(SignatureAlgorithm.HS256, key)
                .setExpiration(new Date(System.currentTimeMillis() + expireAfterSeconds * 1000L))
                .compact();

        return jwt;
    }

//    生成加密token字符串，使用默认时长（expire：24小时）
    public String generateToken(Map<String, Object> claims){
        return generateToken(claims, expire);
    }

//    解析加密token字符串，获取claims
    public Claims parseToken(String token){
        return Jwts.parser()
                .setSigningKey(key)
                .parseClaimsJws(
                        parseTokenStr(token)
                )
                .getBody();
    }
    public static Claims parseTokenStatic(String token){
        return Jwts.parser()
                .setSigningKey(key)
                .parseClaimsJws(
                        parseTokenStr(token)
                )
                .getBody();
    }

//    净化token字符串（未解密），只是去除了Bearer前缀
    public static String parseTokenStr(String tokenStr){
        return tokenStr.startsWith("Bearer ")
                ? tokenStr.split("Bearer ")[1]
                : tokenStr;
    }

//    从加密token中获取指定字段的值，包含了解析字符串和净化字符串
    public <T> T getTokenField(String token, String fieldName){
        return (T) parseToken(token).get(fieldName);
    }

//    获取所有直接访问的URL，包含了用户、话题、主页和其他的直接访问URL
    public String[] getDirectAccessUrl() {
        int totalLength = userDirectAccessUrl.length
                + topicDirectAccessUrl.length
                + homeDirectAccessUrl.length
                + otherDirectAccessUrl.length;

        // 创建结果数组
        String[] result = new String[totalLength];
        int position = 0;

        if (userDirectAccessUrl.length > 0) {
            System.arraycopy(userDirectAccessUrl, 0, result, position, userDirectAccessUrl.length);
            position += userDirectAccessUrl.length;
        }
        if (topicDirectAccessUrl.length > 0) {
            System.arraycopy(topicDirectAccessUrl, 0, result, position, topicDirectAccessUrl.length);
            position += topicDirectAccessUrl.length;
        }
        if (homeDirectAccessUrl.length > 0) {
            System.arraycopy(homeDirectAccessUrl, 0, result, position, homeDirectAccessUrl.length);
            position += homeDirectAccessUrl.length;
        }
        if (otherDirectAccessUrl.length > 0) {
            System.arraycopy(otherDirectAccessUrl, 0, result, position, otherDirectAccessUrl.length);
        }
        return result;
    }

//    处理某些跨域请求，设置响应头，允许跨域访问，并返回处理后的响应对象
    public HttpServletResponse handleResponse(HttpServletRequest request, HttpServletResponse response){
        String origin = request.getHeader("Origin");
        response.setHeader("Access-Control-Allow-Origin", origin);
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Allow-Headers", "x-requested-with,Authorization,token,Origin,Content-Type,Accept");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=UTF-8");
        return response;
    }

}
