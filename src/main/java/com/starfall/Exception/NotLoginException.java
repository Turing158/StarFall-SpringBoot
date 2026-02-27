package com.starfall.Exception;

public class NotLoginException extends RuntimeException{
    public NotLoginException(String message) {
        super(message);
    }
}
