package com.starfall.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.Random;

@Component
public class CodeUtil {
    @Autowired
    RedisUtil redisUtil;

    public static String getCode(int length){
        char[] num_letter = "abcdefghijklmnobqrstuvwxyz23456789".toCharArray();
        Random r = new Random();
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < length; i++) {
            code.append(num_letter[r.nextInt(num_letter.length)]);
        }
        return code.toString();
    }

    public static byte[] getBase64Bytes(String base64){
        String avatarOutHead = "base64,";
        String[] base64Split = base64.split(avatarOutHead);
        if(base64Split.length > 1){
            base64 = base64Split[1];
        }

        byte[] bytes = Base64.getDecoder().decode(base64);
        for (int i = 0; i < bytes.length; ++i) {
            if (bytes[i] < 0) {// 调整异常数据
                bytes[i] += 256;
            }
        }
        return bytes;
    }

    public boolean checkCode(String codeStr){
        return checkCode(codeStr,true);
    }

    public boolean checkCode(String codeStr,boolean isCheckDelete){
        String[] codeSplit = codeStr.split(":");
        if(codeSplit.length != 2){
            return false;
        }
        String key = "code:"+codeSplit[0];
        if(!redisUtil.hasKey(key)){
            return false;
        }
        boolean result = codeSplit[1].equals(redisUtil.get(key, String.class));
        if(result && isCheckDelete){
            redisUtil.delete(key);
        }
        return result;
    }

    public boolean checkEmailCode(String keyPack,String email,String emailCode){
        return checkEmailCode(keyPack,email,emailCode,false);
    }

    public boolean checkEmailCode(String keyPack,String email,String emailCode,boolean isCheckDelete){
        String key = keyPack+email;
        if(!redisUtil.hasKey(key)){
            return false;
        }
        boolean result = emailCode.equalsIgnoreCase(redisUtil.get(key, String.class));
        if(result && isCheckDelete){
            redisUtil.delete(key);
        }
        return result;
    }
}
