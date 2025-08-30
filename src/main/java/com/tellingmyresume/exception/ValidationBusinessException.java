package com.tellingmyresume.exception;

import org.springframework.http.HttpStatus;

public abstract class ValidationBusinessException extends BaseApplicationException {
    
    protected ValidationBusinessException(String message, HttpStatus httpStatus, String errorCode) {
        super(message, httpStatus, errorCode);
    }
    
    protected ValidationBusinessException(String message, Throwable cause, HttpStatus httpStatus, String errorCode) {
        super(message, cause, httpStatus, errorCode);
    }
    
    @Override
    public String getCategory() {
        return "VALIDATION";
    }
}