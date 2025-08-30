package com.tellingmyresume.exception;

import com.tellingmyresume.constants.ErrorCodes;
import org.springframework.http.HttpStatus;

public abstract class AIServiceException extends AIServiceBusinessException {
    
    public AIServiceException(String aiProvider, String message, String errorCode) {
        super(aiProvider, message, HttpStatus.SERVICE_UNAVAILABLE, errorCode);
    }
    
    public AIServiceException(String aiProvider, String message, Throwable cause, String errorCode) {
        super(aiProvider, message, cause, HttpStatus.SERVICE_UNAVAILABLE, errorCode);
    }
}