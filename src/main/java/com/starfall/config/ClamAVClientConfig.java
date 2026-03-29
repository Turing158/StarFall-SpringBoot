package com.starfall.config;


import fi.solita.clamav.ClamAVClient;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "clamav")
@Data
public class ClamAVClientConfig {
    private String url;
    private int port;
    private int timeout;

    @Bean
    public ClamAVClient clamAVClient() {
        return new ClamAVClient(url, port, timeout);
    }

}
