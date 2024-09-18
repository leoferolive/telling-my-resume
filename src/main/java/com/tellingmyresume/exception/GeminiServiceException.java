package com.tellingmyresume.exception;

public class GeminiServiceException extends RuntimeException {
	private static final long serialVersionUID = 5706305532153892666L;

	public GeminiServiceException(String message) {
        super(message);
    }

    public GeminiServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
