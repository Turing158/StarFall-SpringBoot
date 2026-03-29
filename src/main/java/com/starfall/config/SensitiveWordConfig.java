package com.starfall.config;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.file.FileReader;
import cn.hutool.dfa.SensitiveUtil;
import com.starfall.util.MinioUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import java.io.File;

@Configuration
@Slf4j
public class SensitiveWordConfig implements InitializingBean {


    @Autowired
    MinioUtil minioUtil;

    @Override
    public void afterPropertiesSet() {
        File file = FileUtil.file("SensitiveWord.txt");
        FileReader reader = new FileReader(file);
        SensitiveUtil.init(reader.readLines());
        log.info("initialize bean:{}", SensitiveWordConfig.class.getName());
    }
}
