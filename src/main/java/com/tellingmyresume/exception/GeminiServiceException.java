package com.tellingmyresume.exception;

import com.tellingmyresume.constants.ErrorCodes;

public class GeminiServiceException extends AIServiceException {
    private static final long serialVersionUID = 5706305532153892666L;
    private static final String AI_PROVIDER = "Gemini";

    public GeminiServiceException(String message) {
        super(AI_PROVIDER, message, ErrorCodes.AI_GEMINI_ERROR);
    }

    public GeminiServiceException(String message, Throwable cause) {
        super(AI_PROVIDER, message, cause, ErrorCodes.AI_GEMINI_ERROR);
    }
}
