package com.tellingmyresume.exception;

import com.tellingmyresume.constants.ErrorCodes;
import org.springframework.http.HttpStatus;

public class InvalidResumeException extends ValidationBusinessException {
	private static final long serialVersionUID = 2239573725527230082L;

	public InvalidResumeException(String message) {
        super(message, HttpStatus.BAD_REQUEST, ErrorCodes.RESUME_INVALID);
    }

    public InvalidResumeException(String message, Throwable cause) {
        super(message, cause, HttpStatus.BAD_REQUEST, ErrorCodes.RESUME_INVALID);
    }
}