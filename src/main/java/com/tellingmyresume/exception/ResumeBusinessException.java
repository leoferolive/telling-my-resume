package com.tellingmyresume.exception;

import org.springframework.http.HttpStatus;

public abstract class ResumeBusinessException extends BaseApplicationException {
    
    protected ResumeBusinessException(String message, HttpStatus httpStatus, String errorCode) {
        super(message, httpStatus, errorCode);
    }
    
    protected ResumeBusinessException(String message, Throwable cause, HttpStatus httpStatus, String errorCode) {
        super(message, cause, httpStatus, errorCode);
    }
    
    @Override
    public String getCategory() {
        return "RESUME";
    }
}