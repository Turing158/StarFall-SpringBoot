package com.starfall.Exception;

import lombok.Data;

@Data
public class ServiceException extends RuntimeException{
    String msg;
    public ServiceException(String msg,String message) {
        super(message);
        this.msg = msg;
    }
}
