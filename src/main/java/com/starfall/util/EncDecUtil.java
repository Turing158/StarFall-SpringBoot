package com.starfall.util;

import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.RSA;
import cn.hutool.crypto.symmetric.AES;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.zip.Deflater;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.Inflater;

@Component
@RefreshScope
@Slf4j
public class EncDecUtil {
    @Value("${aes.key}")
    private String aesKey = "StarFallSecureKeyAndTheKeyMust32";
    @Value("${rsa.key.private}")
    private String rsaPrivateKey = "";
    @Value("${rsa.key.public}")
    private String rsaPublicKey = "";

    private AES aes;
    private static RSA rsa;

    @PostConstruct
    public void init() {
        aes = SecureUtil.aes(SecureUtil.generateKey("AES", aesKey.getBytes()).getEncoded());
        rsa = SecureUtil.rsa(rsaPrivateKey,rsaPublicKey);
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

    public String rsaEncrypt(String str){
        return rsa.encryptBase64(str, KeyType.PublicKey);
    }

    public String rsaDecrypt(String str) {
        return rsa.decryptStr(str, KeyType.PrivateKey);
    }

    public static String rsaDecryptStatic(String str) {
        return rsa.decryptStr(str, KeyType.PrivateKey);
    }
}
