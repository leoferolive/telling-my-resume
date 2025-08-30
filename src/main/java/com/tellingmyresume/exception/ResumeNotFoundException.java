package com.tellingmyresume.exception;

import com.tellingmyresume.constants.ErrorCodes;
import org.springframework.http.HttpStatus;

public class ResumeNotFoundException extends ResumeBusinessException {
    
	private static final long serialVersionUID = -6974058599796065210L;

	public ResumeNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND, ErrorCodes.RESUME_NOT_FOUND);
    }

    public ResumeNotFoundException(String message, Throwable cause) {
        super(message, cause, HttpStatus.NOT_FOUND, ErrorCodes.RESUME_NOT_FOUND);
    }
}
