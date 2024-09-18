package com.tellingmyresume.exception;

public class ResumeStorageException extends RuntimeException {

	private static final long serialVersionUID = 5802486019003212938L;

	public ResumeStorageException(String message) {
        super(message);
    }

    public ResumeStorageException(String message, Throwable cause) {
        super(message, cause);
    }
	
}
