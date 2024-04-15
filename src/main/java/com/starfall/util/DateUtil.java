package com.starfall.util;

import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class DateUtil {
    public static boolean isContinuityOfDate(LocalDate date1, LocalDate date2){
        return date1.plusDays(1).equals(date2);
    }
    public static String fillZero(int num){
        if(num < 10){
            return "0"+num;
        }
        return ""+num;
    }
}
