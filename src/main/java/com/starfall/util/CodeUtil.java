package com.starfall.util;

import java.util.Random;

public class CodeUtil {
    public static String getCode(int length){
        char[] num_letter = "abcdefghijklmnobqrstuvwxyz23456789".toCharArray();
        Random r = new Random();
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < length; i++) {
            code.append(num_letter[r.nextInt(num_letter.length)]);
        }
        return code.toString();
    }
}
