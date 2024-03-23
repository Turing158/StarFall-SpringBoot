package com.starfall.util;

public class Exp {
    public static int getMaxExp(int level){
        switch (level){
            case 1:
                return 100;
            case 2:
                return 530;
            case 3:
                return 1200;
            case 4:
                return 2140;
            case 5:
                return 3310;
            case 6:
                return 4710;
            case 7:
                return 6340;
            case 8:
                return 8200;
            case 9:
                return 10290;
            case 10:
                return 12610;
        }
        return 999999;
    }

    public static int checkAndLevelUp(int exp,int level){
        int maxExp = getMaxExp(level);
        return exp - maxExp;
    }
}
