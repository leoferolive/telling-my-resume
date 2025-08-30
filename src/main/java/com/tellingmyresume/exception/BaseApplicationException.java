package com.tellingmyresume.exception;

import org.springframework.http.HttpStatus;

public abstract class BaseApplicationException extends RuntimeException {
    
    private final HttpStatus httpStatus;
    private final String errorCode;
    private final String correlationId;
    
    protected BaseApplicationException(String message, HttpStatus httpStatus, String errorCode) {
        super(message);
        this.httpStatus = httpStatus;
        this.errorCode = errorCode;
        this.correlationId = com.tellingmyresume.utils.CorrelationIdUtils.getCorrelationId();
    }
    
    protected BaseApplicationException(String message, Throwable cause, HttpStatus httpStatus, String errorCode) {
        super(message, cause);
        this.httpStatus = httpStatus;
        this.errorCode = errorCode;
        this.correlationId = com.tellingmyresume.utils.CorrelationIdUtils.getCorrelationId();
    }
    
    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
    
    public String getErrorCode() {
        return errorCode;
    }
    
    public String getCorrelationId() {
        return correlationId;
    }
    
    public abstract String getCategory();
}