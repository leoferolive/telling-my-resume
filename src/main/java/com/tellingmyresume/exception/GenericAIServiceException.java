package com.tellingmyresume.exception;

import com.tellingmyresume.constants.ErrorCodes;

public class GenericAIServiceException extends AIServiceException {
    
    public GenericAIServiceException(String aiProvider, String message) {
        super(aiProvider, message, ErrorCodes.AI_SERVICE_UNAVAILABLE);
    }
    
    public GenericAIServiceException(String aiProvider, String message, Throwable cause) {
        super(aiProvider, message, cause, ErrorCodes.AI_SERVICE_UNAVAILABLE);
    }
}