package com.tellingmyresume.exception;

import org.springframework.http.HttpStatus;

public abstract class AIServiceBusinessException extends BaseApplicationException {
    
    private final String aiProvider;
    
    protected AIServiceBusinessException(String aiProvider, String message, HttpStatus httpStatus, String errorCode) {
        super(message, httpStatus, errorCode);
        this.aiProvider = aiProvider;
    }
    
    protected AIServiceBusinessException(String aiProvider, String message, Throwable cause, HttpStatus httpStatus, String errorCode) {
        super(message, cause, httpStatus, errorCode);
        this.aiProvider = aiProvider;
    }
    
    public String getAiProvider() {
        return aiProvider;
    }
    
    @Override
    public String getCategory() {
        return "AI_SERVICE";
    }
}