package com.starfall.Exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdminServiceException extends RuntimeException{
    String msg;
    public AdminServiceException(String msg,String message) {
        super(message);
        this.msg = msg;
    }
}
