package com.tellingmyresume.exception;

import com.tellingmyresume.constants.ErrorCodes;

public class ClaudeServiceException extends AIServiceException {
    
    private static final String AI_PROVIDER = "Claude";
    
    public ClaudeServiceException(String message) {
        super(AI_PROVIDER, message, ErrorCodes.AI_CLAUDE_ERROR);
    }
    
    public ClaudeServiceException(String message, Throwable cause) {
        super(AI_PROVIDER, message, cause, ErrorCodes.AI_CLAUDE_ERROR);
    }
}