package com.starfall.Exception;

import lombok.Data;

@Data
public class PermissionException extends RuntimeException{
    String msg;
    public PermissionException(String msg,String message) {
        super(message);
        this.msg = msg;
    }
}
