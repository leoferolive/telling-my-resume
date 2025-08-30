package com.tellingmyresume.exception;

import org.springframework.http.HttpStatus;

public abstract class StorageBusinessException extends BaseApplicationException {
    
    protected StorageBusinessException(String message, HttpStatus httpStatus, String errorCode) {
        super(message, httpStatus, errorCode);
    }
    
    protected StorageBusinessException(String message, Throwable cause, HttpStatus httpStatus, String errorCode) {
        super(message, cause, httpStatus, errorCode);
    }
    
    @Override
    public String getCategory() {
        return "STORAGE";
    }
}