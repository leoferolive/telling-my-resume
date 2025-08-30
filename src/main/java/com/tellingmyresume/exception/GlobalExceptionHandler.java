package com.tellingmyresume.exception;

import com.tellingmyresume.dto.response.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);
	
    @ExceptionHandler(ResumeNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResumeNotFound(ResumeNotFoundException ex, WebRequest request) {
        ErrorResponse error = new ErrorResponse(
            "Resume Not Found",
            ex.getMessage(),
            HttpStatus.NOT_FOUND.value(),
            request.getDescription(false).replace("uri=", "")
        );
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ResumeStorageException.class)
    public ResponseEntity<ErrorResponse> handleResumeStorageError(ResumeStorageException ex, WebRequest request) {
        ErrorResponse error = new ErrorResponse(
            "Storage Error",
            ex.getMessage(),
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            request.getDescription(false).replace("uri=", "")
        );
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(GeminiServiceException.class)
    public ResponseEntity<ErrorResponse> handleGeminiServiceException(GeminiServiceException ex, WebRequest request) {
        ErrorResponse error = new ErrorResponse(
            "Gemini Service Error",
            "Erro no serviço Gemini: " + ex.getMessage(),
            HttpStatus.SERVICE_UNAVAILABLE.value(),
            request.getDescription(false).replace("uri=", "")
        );
        return new ResponseEntity<>(error, HttpStatus.SERVICE_UNAVAILABLE);
    }
    
    @ExceptionHandler(ClaudeServiceException.class)
    public ResponseEntity<ErrorResponse> handleClaudeServiceException(ClaudeServiceException ex, WebRequest request) {
        ErrorResponse error = new ErrorResponse(
            "Claude Service Error",
            "Erro no serviço Claude: " + ex.getMessage(),
            HttpStatus.SERVICE_UNAVAILABLE.value(),
            request.getDescription(false).replace("uri=", "")
        );
        return new ResponseEntity<>(error, HttpStatus.SERVICE_UNAVAILABLE);
    }
    
    @ExceptionHandler(AIServiceException.class)
    public ResponseEntity<ErrorResponse> handleAIServiceException(AIServiceException ex, WebRequest request) {
        ErrorResponse error = new ErrorResponse(
            "AI Service Error",
            "Erro no serviço de IA " + ex.getAiProvider() + ": " + ex.getMessage(),
            HttpStatus.SERVICE_UNAVAILABLE.value(),
            request.getDescription(false).replace("uri=", "")
        );
        return new ResponseEntity<>(error, HttpStatus.SERVICE_UNAVAILABLE);
    }
    
    @ExceptionHandler(StorageException.class)
    public ResponseEntity<ErrorResponse> handleStorageException(StorageException ex, WebRequest request) {
        ErrorResponse error = new ErrorResponse(
            "Storage Error",
            "Erro de armazenamento: " + ex.getMessage(),
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            request.getDescription(false).replace("uri=", "")
        );
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(InvalidResumeException.class)
    public ResponseEntity<ErrorResponse> handleInvalidResumeException(InvalidResumeException ex, WebRequest request) {
        ErrorResponse error = new ErrorResponse(
            "Invalid Resume",
            "Currículo inválido: " + ex.getMessage(),
            HttpStatus.BAD_REQUEST.value(),
            request.getDescription(false).replace("uri=", "")
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }
	
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex, WebRequest request) {
        List<String> details = ex.getBindingResult().getFieldErrors().stream()
            .map(error -> error.getField() + ": " + error.getDefaultMessage())
            .collect(Collectors.toList());
            
        ErrorResponse error = new ErrorResponse(
            "Validation Failed",
            "Dados inválidos fornecidos",
            details,
            HttpStatus.BAD_REQUEST.value(),
            request.getDescription(false).replace("uri=", "")
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex, WebRequest request) {
        LOGGER.error("Ocorreu um erro: ", ex);
        ErrorResponse error = new ErrorResponse(
            "Internal Server Error",
            "Ocorreu um erro inesperado no sistema.",
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            request.getDescription(false).replace("uri=", "")
        );
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException ex, WebRequest request) {
        ErrorResponse error = new ErrorResponse(
            "Runtime Error",
            ex.getMessage(),
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            request.getDescription(false).replace("uri=", "")
        );
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
