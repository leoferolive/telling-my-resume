package com.tellingmyresume.exception;

public class GeminiServiceException extends AIServiceException {
    private static final long serialVersionUID = 5706305532153892666L;
    private static final String AI_PROVIDER = "Gemini";

    public GeminiServiceException(String message) {
        super(AI_PROVIDER, message);
    }

    public GeminiServiceException(String message, Throwable cause) {
        super(AI_PROVIDER, message, cause);
    }
}
