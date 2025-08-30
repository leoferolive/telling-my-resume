package com.tellingmyresume.exception;

import com.tellingmyresume.constants.ErrorCodes;
import org.springframework.http.HttpStatus;

public class ResumeStorageException extends StorageBusinessException {

	public ResumeStorageException(String message) {
        super(message, HttpStatus.INTERNAL_SERVER_ERROR, ErrorCodes.STORAGE_SAVE_ERROR);
    }

    public ResumeStorageException(String message, Throwable cause) {
        super(message, cause, HttpStatus.INTERNAL_SERVER_ERROR, ErrorCodes.STORAGE_SAVE_ERROR);
    }
}
