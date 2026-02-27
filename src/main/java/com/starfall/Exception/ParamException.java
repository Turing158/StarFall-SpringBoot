package com.starfall.Exception;

import lombok.Data;

@Data
public class ParamException extends RuntimeException{
    String msg;
    public ParamException(String msg,String message) {
        super(message);
        this.msg = msg;
    }
}
