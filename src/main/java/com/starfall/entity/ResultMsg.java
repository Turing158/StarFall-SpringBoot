package com.starfall.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResultMsg {
    String msg;
    Object object;
    int num;
    public ResultMsg(String msg,Object object) {
        this.msg = msg;
        this.object = object;
    }

    public static ResultMsg success() {
        return new ResultMsg("SUCCESS",null);
    }
    public static ResultMsg success(int num) {
        return new ResultMsg("SUCCESS",null,num);
    }
    public static ResultMsg success(Object object) {
        return new ResultMsg("SUCCESS",object);
    }
    public static ResultMsg success(Object object,int num) {
        return new ResultMsg("SUCCESS",object,num);
    }
    public static ResultMsg warning(String msg) {
        return new ResultMsg(msg,null);
    }
    public static ResultMsg warning(String msg,Object object) {
        return new ResultMsg(msg,object);
    }
    public static ResultMsg warning(String msg,int num) {
        return new ResultMsg(msg,null,num);
    }
    public static ResultMsg warning(String msg,Object object,int num) {
        return new ResultMsg(msg,object,num);
    }
    public static ResultMsg error(String msg) {
        return new ResultMsg(msg,"ERROR_ERROR",-1);
    }
}
