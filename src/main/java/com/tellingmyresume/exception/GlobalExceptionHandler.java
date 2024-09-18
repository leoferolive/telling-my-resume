package com.tellingmyresume.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);
	
    @ExceptionHandler(ResumeNotFoundException.class)
    public ResponseEntity<String> handleResumeNotFound(ResumeNotFoundException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ResumeStorageException.class)
    public ResponseEntity<String> handleResumeStorageError(ResumeStorageException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // Tratamento de exceção personalizada para API do Gemini
    @ExceptionHandler(GeminiServiceException.class)
    public ResponseEntity<String> handleGeminiServiceException(GeminiServiceException ex) {
        return new ResponseEntity<>("Erro ao processar a requisição: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // Tratamento de exceção para currículos inválidos
    @ExceptionHandler(InvalidResumeException.class)
    public ResponseEntity<String> handleInvalidResumeException(InvalidResumeException ex) {
        return new ResponseEntity<>("Currículo inválido: " + ex.getMessage(), HttpStatus.BAD_REQUEST);
    }
	
    // Tratamento de exceção genérica (fallback)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGenericException(Exception ex) {
    	// Logar a exceção para depuração
    	LOGGER.error("Ocorreu um erro: ", ex);
    	return new ResponseEntity<>("Ocorreu um erro inesperado no sistema.", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
