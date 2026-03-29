package com.starfall.util;

import fi.solita.clamav.ClamAVClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Component
@Slf4j
public class ClamAvUtil {
    @Autowired
    ClamAVClient clamAVClient;

    public boolean scanFile(MultipartFile file){
        try {
            InputStream inputStream = file.getInputStream();
            byte[] scan = clamAVClient.scan(inputStream);
            String res = new String(scan, StandardCharsets.UTF_8);
            if(res.startsWith("stream: OK")){
                log.info("【ClamAvUtil】文件扫描通过！");
                return true;
            }else if(res.startsWith("stream: Can't allocate memory ERROR")){
                log.error("【ClamAvUtil】上传文件超过杀毒软件最大文件限制！");
                return false;
            }else {
                log.error("【ClamAvUtil】文件扫描失败！");
                return false;
            }
        } catch (IOException e) {
            try {
                clamAVClient.ping();
            } catch (IOException ioException) {
                log.error("【ClamAvUtil】连接杀毒服务异常！");
                return false;
            }
            log.error("【ClamAvUtil】文件扫描失败！{}",e.getMessage());
            return false;
        }

    }
}
