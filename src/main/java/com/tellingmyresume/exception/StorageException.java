package com.tellingmyresume.exception;

import com.tellingmyresume.constants.ErrorCodes;
import org.springframework.http.HttpStatus;

public class StorageException extends StorageBusinessException {
    
    public StorageException(String message) {
        super(message, HttpStatus.INTERNAL_SERVER_ERROR, ErrorCodes.STORAGE_SAVE_ERROR);
    }
    
    public StorageException(String message, Throwable cause) {
        super(message, cause, HttpStatus.INTERNAL_SERVER_ERROR, ErrorCodes.STORAGE_SAVE_ERROR);
    }
}