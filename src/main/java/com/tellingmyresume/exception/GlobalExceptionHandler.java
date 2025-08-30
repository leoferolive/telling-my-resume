package com.tellingmyresume.exception;

import com.tellingmyresume.constants.ErrorCodes;
import com.tellingmyresume.constants.ErrorMessages;
import com.tellingmyresume.dto.response.ErrorResponse;
import com.tellingmyresume.service.MessageService;
import com.tellingmyresume.utils.CorrelationIdUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);
	
	private final MessageService messageService;
	
	public GlobalExceptionHandler(MessageService messageService) {
		this.messageService = messageService;
	}
	
	// === Resume Related Exceptions ===
	
    @ExceptionHandler(ResumeNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResumeNotFound(ResumeNotFoundException ex, WebRequest request) {
        LOGGER.warn("Resume not found - correlationId: {}, message: {}", 
            CorrelationIdUtils.getCorrelationId(), ex.getMessage());
            
        String message = messageService.getMessage("error.resume.not_found", new Object[]{ex.getMessage()});
        return buildErrorResponse("Resume Not Found", message, 
            HttpStatus.NOT_FOUND, ErrorCodes.RESUME_NOT_FOUND, request);
    }

    @ExceptionHandler(InvalidResumeException.class)
    public ResponseEntity<ErrorResponse> handleInvalidResumeException(InvalidResumeException ex, WebRequest request) {
        LOGGER.warn("Invalid resume - correlationId: {}, message: {}", 
            CorrelationIdUtils.getCorrelationId(), ex.getMessage());
            
        String message = messageService.getMessage("error.resume.invalid", new Object[]{ex.getMessage()});
        return buildErrorResponse("Invalid Resume", message, 
            HttpStatus.BAD_REQUEST, ErrorCodes.RESUME_INVALID, request);
    }
    
    // === Storage Related Exceptions ===

    @ExceptionHandler({ResumeStorageException.class, StorageException.class})
    public ResponseEntity<ErrorResponse> handleStorageException(RuntimeException ex, WebRequest request) {
        LOGGER.error("Storage error - correlationId: {}, message: {}", 
            CorrelationIdUtils.getCorrelationId(), ex.getMessage(), ex);
            
        String message = messageService.getMessage("error.storage.save_error", new Object[]{ex.getMessage()});
        return buildErrorResponse("Storage Error", message, 
            HttpStatus.INTERNAL_SERVER_ERROR, ErrorCodes.STORAGE_SAVE_ERROR, request);
    }
    
    // === AI Service Related Exceptions ===

    @ExceptionHandler(ClaudeServiceException.class)
    public ResponseEntity<ErrorResponse> handleClaudeServiceException(ClaudeServiceException ex, WebRequest request) {
        LOGGER.error("Claude service error - correlationId: {}, provider: {}, message: {}", 
            CorrelationIdUtils.getCorrelationId(), ex.getAiProvider(), ex.getMessage(), ex);
            
        return buildErrorResponse("AI Service Error", ErrorMessages.CLAUDE_API_ERROR + ex.getMessage(), 
            HttpStatus.SERVICE_UNAVAILABLE, ErrorCodes.AI_CLAUDE_ERROR, request);
    }
    
    @ExceptionHandler(GeminiServiceException.class)
    public ResponseEntity<ErrorResponse> handleGeminiServiceException(GeminiServiceException ex, WebRequest request) {
        LOGGER.error("Gemini service error - correlationId: {}, provider: {}, message: {}", 
            CorrelationIdUtils.getCorrelationId(), ex.getAiProvider(), ex.getMessage(), ex);
            
        return buildErrorResponse("AI Service Error", ErrorMessages.GEMINI_API_ERROR, 
            HttpStatus.SERVICE_UNAVAILABLE, ErrorCodes.AI_GEMINI_ERROR, request);
    }
    
    @ExceptionHandler(AIServiceException.class)
    public ResponseEntity<ErrorResponse> handleAIServiceException(AIServiceException ex, WebRequest request) {
        LOGGER.error("AI service error - correlationId: {}, provider: {}, message: {}", 
            CorrelationIdUtils.getCorrelationId(), ex.getAiProvider(), ex.getMessage(), ex);
            
        return buildErrorResponse("AI Service Error", 
            "Erro no serviço de IA " + ex.getAiProvider() + ": " + ex.getMessage(), 
            HttpStatus.SERVICE_UNAVAILABLE, ErrorCodes.AI_SERVICE_UNAVAILABLE, request);
    }
    
    // === Validation Related Exceptions ===
	
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex, WebRequest request) {
        List<String> details = ex.getBindingResult().getFieldErrors().stream()
            .map(error -> error.getField() + ": " + error.getDefaultMessage())
            .collect(Collectors.toList());
        
        LOGGER.warn("Validation failed - correlationId: {}, errors: {}", 
            CorrelationIdUtils.getCorrelationId(), details);
            
        String message = messageService.getMessage("error.validation.failed");
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setError("Validation Failed");
        errorResponse.setMessage(message);
        errorResponse.setStatus(HttpStatus.BAD_REQUEST.value());
        errorResponse.setPath(extractPath(request));
        errorResponse.setCorrelationId(CorrelationIdUtils.getOrGenerateCorrelationId());
        errorResponse.setDetails(details);
        
        return ResponseEntity.badRequest().body(errorResponse);
    }
    
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, WebRequest request) {
        LOGGER.warn("Invalid JSON request - correlationId: {}, message: {}", 
            CorrelationIdUtils.getCorrelationId(), ex.getMessage());
            
        return buildErrorResponse("Invalid Request", "Formato de dados inválido", 
            HttpStatus.BAD_REQUEST, ErrorCodes.REQUEST_INVALID, request);
    }
    
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingParameter(MissingServletRequestParameterException ex, WebRequest request) {
        LOGGER.warn("Missing parameter - correlationId: {}, parameter: {}", 
            CorrelationIdUtils.getCorrelationId(), ex.getParameterName());
            
        return buildErrorResponse("Missing Parameter", 
            "Parâmetro obrigatório ausente: " + ex.getParameterName(), 
            HttpStatus.BAD_REQUEST, ErrorCodes.REQUEST_INVALID, request);
    }
    
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex, WebRequest request) {
        LOGGER.warn("Type mismatch - correlationId: {}, parameter: {}, expected: {}", 
            CorrelationIdUtils.getCorrelationId(), ex.getName(), 
            ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "unknown");
            
        return buildErrorResponse("Invalid Parameter Type", 
            "Tipo inválido para o parâmetro: " + ex.getName(), 
            HttpStatus.BAD_REQUEST, ErrorCodes.REQUEST_INVALID, request);
    }
    
    // === HTTP Method Related Exceptions ===
    
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex, WebRequest request) {
        LOGGER.warn("Method not supported - correlationId: {}, method: {}", 
            CorrelationIdUtils.getCorrelationId(), ex.getMethod());
            
        return buildErrorResponse("Method Not Supported", 
            "Método HTTP não suportado: " + ex.getMethod(), 
            HttpStatus.METHOD_NOT_ALLOWED, ErrorCodes.REQUEST_INVALID, request);
    }
    
    // === File Upload Related Exceptions ===
    
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ErrorResponse> handleMaxUploadSize(MaxUploadSizeExceededException ex, WebRequest request) {
        LOGGER.warn("File too large - correlationId: {}, maxSize: {}", 
            CorrelationIdUtils.getCorrelationId(), ex.getMaxUploadSize());
            
        return buildErrorResponse("File Too Large", 
            "Arquivo muito grande. Tamanho máximo permitido: " + ex.getMaxUploadSize() + " bytes", 
            HttpStatus.PAYLOAD_TOO_LARGE, ErrorCodes.VALIDATION_ERROR, request);
    }
    
    // === Generic Exception Handlers ===
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex, WebRequest request) {
        LOGGER.error("Unexpected error - correlationId: {}, type: {}, message: {}", 
            CorrelationIdUtils.getCorrelationId(), ex.getClass().getSimpleName(), ex.getMessage(), ex);
            
        String message = messageService.getMessage("error.internal_server");
        return buildErrorResponse("Internal Server Error", message, 
            HttpStatus.INTERNAL_SERVER_ERROR, ErrorCodes.INTERNAL_ERROR, request);
    }
    
    // === Helper Methods ===
    
    private ResponseEntity<ErrorResponse> buildErrorResponse(String error, String message, 
            HttpStatus status, String errorCode, WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setError(error);
        errorResponse.setMessage(message);
        errorResponse.setStatus(status.value());
        errorResponse.setPath(extractPath(request));
        errorResponse.setCorrelationId(CorrelationIdUtils.getOrGenerateCorrelationId());
        
        return new ResponseEntity<>(errorResponse, status);
    }
    
    private String extractPath(WebRequest request) {
        return request.getDescription(false).replace("uri=", "");
    }
}
