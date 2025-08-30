package com.tellingmyresume.exception;

public abstract class AIServiceException extends Exception {
    
    private final String aiProvider;
    
    public AIServiceException(String aiProvider, String message) {
        super(message);
        this.aiProvider = aiProvider;
    }
    
    public AIServiceException(String aiProvider, String message, Throwable cause) {
        super(message, cause);
        this.aiProvider = aiProvider;
    }
    
    public String getAiProvider() {
        return aiProvider;
    }
}