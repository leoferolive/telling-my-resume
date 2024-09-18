package com.tellingmyresume.exception;

public class InvalidResumeException extends RuntimeException {
	private static final long serialVersionUID = 2239573725527230082L;

	public InvalidResumeException(String message) {
        super(message);
    }
}