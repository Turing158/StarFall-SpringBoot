package com.starfall.util;

import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.symmetric.AES;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

@Component
@RefreshScope
@Slf4j
public class EncDecUtil {
    @Value("${aes.key}")
    private String aesKey = "StarFallSecureKeyAndTheKeyMust32";

    private AES aes;

    @PostConstruct
    public void init() {
        aes = SecureUtil.aes(SecureUtil.generateKey("AES", aesKey.getBytes()).getEncoded());
    }

    public String aesEncrypt(String str) {
        return aesEncrypt(str,false);
    }

    public String aesEncrypt(String str,boolean isBase64) {
        return isBase64 ? aes.encryptBase64(str) : aes.encryptHex(str);
    }

    public String aesDecrypt(String str) {
        return aes.decryptStr(str);
    }
}
