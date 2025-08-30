package com.tellingmyresume.utils;

import java.util.UUID;

public final class CorrelationIdUtils {
    
    public static final String CORRELATION_ID_HEADER = "X-Correlation-ID";
    private static final ThreadLocal<String> CORRELATION_ID = new ThreadLocal<>();
    
    private CorrelationIdUtils() {
        // Utility class
    }
    
    public static String generateCorrelationId() {
        return UUID.randomUUID().toString();
    }
    
    public static void setCorrelationId(String correlationId) {
        CORRELATION_ID.set(correlationId);
    }
    
    public static String getCorrelationId() {
        return CORRELATION_ID.get();
    }
    
    public static String getOrGenerateCorrelationId() {
        String correlationId = getCorrelationId();
        if (correlationId == null) {
            correlationId = generateCorrelationId();
            setCorrelationId(correlationId);
        }
        return correlationId;
    }
    
    public static void clearCorrelationId() {
        CORRELATION_ID.remove();
    }
}