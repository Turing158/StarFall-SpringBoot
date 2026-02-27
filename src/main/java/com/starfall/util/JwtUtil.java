package com.starfall.util;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import java.util.stream.Stream;

@Component
@RefreshScope
public class JwtUtil {
    @Value("${jwt.key}")
    private static String key = "StarFall";
    @Value("${jwt.expire}")
    private static final Long expire = 24*60*60*1000L;

    @Value("${direct.url.user}")
    private String[] userDirectAccessUrl = { };
    @Value("${direct.url.topic}")
    private String[] topicDirectAccessUrl = { };
    @Value("${direct.url.home}")
    private String[] homeDirectAccessUrl = { };
    @Value("${direct.url.other}")
    private String[] otherDirectAccessUrl = { };


    public static String generateJwt(Map<String, Object> claims){
        return Jwts.builder()
                .addClaims(claims)
                .signWith(SignatureAlgorithm.HS256, key)
                .setExpiration(new Date(System.currentTimeMillis() + expire))
                .compact();
    }

    public static String generateJwt(Map<String, Object> claims, long expireAfterSeconds){
        return Jwts.builder()
                .addClaims(claims)
                .signWith(SignatureAlgorithm.HS256, key)
                .setExpiration(new Date(System.currentTimeMillis() + expireAfterSeconds * 1000L))
                .compact();
    }


    public static Claims parseJWT(String jwt){
        return Jwts.parser()
                .setSigningKey(key)
                .parseClaimsJws(jwt)
                .getBody();
    }

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
