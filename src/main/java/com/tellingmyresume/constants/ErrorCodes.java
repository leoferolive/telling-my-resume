package com.tellingmyresume.constants;

public final class ErrorCodes {
    
    private ErrorCodes() {
        // Utility class
    }
    
    // Resume related errors
    public static final String RESUME_NOT_FOUND = "RESUME_001";
    public static final String RESUME_INVALID = "RESUME_002";
    public static final String RESUME_PROCESSING_ERROR = "RESUME_003";
    
    // Storage related errors
    public static final String STORAGE_SAVE_ERROR = "STORAGE_001";
    public static final String STORAGE_READ_ERROR = "STORAGE_002";
    public static final String STORAGE_DELETE_ERROR = "STORAGE_003";
    public static final String STORAGE_FILE_EXISTS = "STORAGE_004";
    
    // AI Service related errors
    public static final String AI_CLAUDE_ERROR = "AI_001";
    public static final String AI_GEMINI_ERROR = "AI_002";
    public static final String AI_SERVICE_UNAVAILABLE = "AI_003";
    public static final String AI_PROVIDER_NOT_FOUND = "AI_004";
    
    // Validation errors
    public static final String VALIDATION_ERROR = "VALIDATION_001";
    public static final String REQUEST_INVALID = "VALIDATION_002";
    
    // System errors
    public static final String INTERNAL_ERROR = "SYSTEM_001";
    public static final String EXTERNAL_SERVICE_ERROR = "SYSTEM_002";
}