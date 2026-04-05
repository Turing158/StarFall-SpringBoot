package com.starfall.util;

import io.netty.handler.codec.DateFormatter;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class DateUtil {
    public boolean isContinuityOfDate(LocalDate previousDate, LocalDate now){
        return previousDate.plusDays(1).equals(now);
    }

    public String fillZero(int num){
        if(num < 10){
            return "0"+num;
        }
        return ""+num;
    }

    public String getDateTimeByFormat(String format){
        return getDateTimeByFormat(LocalDateTime.now(),format);
    }

    public String getDateTimeByFormat(LocalDateTime ldt, String format){
        return ldt.format(DateTimeFormatter.ofPattern(format));
    }

    public boolean isExpire(String expireTime){
        return isExpire(LocalDateTime.parse(expireTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
    }
    public boolean isExpire(LocalDateTime expireTime){
        return LocalDateTime.now().isAfter(expireTime);
    }
}
