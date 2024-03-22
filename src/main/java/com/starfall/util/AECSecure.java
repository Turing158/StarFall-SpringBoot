package com.starfall.util;

import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.symmetric.AES;
import org.springframework.stereotype.Component;

@Component
public class AECSecure {
    private final String key = "StarFallSecureKeyAndTheKeyMust32";
    private final AES aes = SecureUtil.aes(SecureUtil.generateKey("AES", key.getBytes()).getEncoded());
    public String encrypt(String str) {
        return aes.encryptHex(str);
    }
    public String decrypt(String str) {
        return aes.decryptStr(str);
    }
}
