package com.tellingmyresume.utils;

import org.slf4j.Logger;
import org.slf4j.MDC;

import java.util.Map;

public final class StructuredLogging {
    
    private StructuredLogging() {
        // Utility class
    }
    
    public static void logWithContext(Logger logger, String level, String message, Map<String, String> context) {
        // Save current MDC
        Map<String, String> originalMdc = MDC.getCopyOfContextMap();
        
        try {
            // Add context to MDC
            if (context != null) {
                context.forEach(MDC::put);
            }
            
            // Add default context
            String correlationId = CorrelationIdUtils.getCorrelationId();
            if (correlationId != null) {
                MDC.put("correlationId", correlationId);
            }
            
            // Log based on level
            switch (level.toUpperCase()) {
                case "ERROR":
                    logger.error(message);
                    break;
                case "WARN":
                    logger.warn(message);
                    break;
                case "INFO":
                    logger.info(message);
                    break;
                case "DEBUG":
                    logger.debug(message);
                    break;
                case "TRACE":
                    logger.trace(message);
                    break;
                default:
                    logger.info(message);
            }
        } finally {
            // Restore original MDC
            MDC.clear();
            if (originalMdc != null) {
                MDC.setContextMap(originalMdc);
            }
        }
    }
    
    // Convenience methods for common logging scenarios
    
    public static void logApiRequest(Logger logger, String method, String endpoint, String userId) {
        Map<String, String> context = Map.of(
            "event_type", "api_request",
            "http_method", method,
            "endpoint", endpoint,
            "user_id", userId != null ? userId : "anonymous"
        );
        logWithContext(logger, "INFO", "API request received", context);
    }
    
    public static void logApiResponse(Logger logger, String method, String endpoint, int statusCode, long durationMs) {
        Map<String, String> context = Map.of(
            "event_type", "api_response",
            "http_method", method,
            "endpoint", endpoint,
            "status_code", String.valueOf(statusCode),
            "duration_ms", String.valueOf(durationMs)
        );
        logWithContext(logger, "INFO", "API response sent", context);
    }
    
    public static void logBusinessEvent(Logger logger, String eventName, String entityType, String entityId, String action) {
        Map<String, String> context = Map.of(
            "event_type", "business_event",
            "event_name", eventName,
            "entity_type", entityType,
            "entity_id", entityId,
            "action", action
        );
        logWithContext(logger, "INFO", "Business event occurred", context);
    }
    
    public static void logExternalServiceCall(Logger logger, String serviceName, String operation, boolean success, long durationMs) {
        Map<String, String> context = Map.of(
            "event_type", "external_service_call",
            "service_name", serviceName,
            "operation", operation,
            "success", String.valueOf(success),
            "duration_ms", String.valueOf(durationMs)
        );
        String level = success ? "INFO" : "WARN";
        String message = String.format("External service call to %s %s", serviceName, success ? "succeeded" : "failed");
        logWithContext(logger, level, message, context);
    }
    
    public static void logError(Logger logger, String errorType, String errorMessage, String component, Throwable throwable) {
        Map<String, String> context = Map.of(
            "event_type", "error",
            "error_type", errorType,
            "component", component,
            "exception_class", throwable != null ? throwable.getClass().getSimpleName() : "Unknown"
        );
        
        // Save current MDC
        Map<String, String> originalMdc = MDC.getCopyOfContextMap();
        
        try {
            // Add context to MDC
            context.forEach(MDC::put);
            
            // Add correlation ID
            String correlationId = CorrelationIdUtils.getCorrelationId();
            if (correlationId != null) {
                MDC.put("correlationId", correlationId);
            }
            
            if (throwable != null) {
                logger.error("Error occurred: {}", errorMessage, throwable);
            } else {
                logger.error("Error occurred: {}", errorMessage);
            }
        } finally {
            // Restore original MDC
            MDC.clear();
            if (originalMdc != null) {
                MDC.setContextMap(originalMdc);
            }
        }
    }
}