package com.tellingmyresume.exception;

public class GenericAIServiceException extends AIServiceException {
    
    public GenericAIServiceException(String aiProvider, String message) {
        super(aiProvider, message);
    }
    
    public GenericAIServiceException(String aiProvider, String message, Throwable cause) {
        super(aiProvider, message, cause);
    }
}