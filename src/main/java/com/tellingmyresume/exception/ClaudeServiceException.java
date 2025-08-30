package com.tellingmyresume.exception;

public class ClaudeServiceException extends AIServiceException {
    
    private static final String AI_PROVIDER = "Claude";
    
    public ClaudeServiceException(String message) {
        super(AI_PROVIDER, message);
    }
    
    public ClaudeServiceException(String message, Throwable cause) {
        super(AI_PROVIDER, message, cause);
    }
}