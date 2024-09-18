package com.tellingmyresume.exception;

public class ResumeNotFoundException extends ResumeStorageException{
    
	private static final long serialVersionUID = -6974058599796065210L;

	public ResumeNotFoundException(String message) {
        super(message);
    }

    public ResumeNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
	
}
