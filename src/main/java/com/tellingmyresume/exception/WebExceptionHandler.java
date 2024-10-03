package com.tellingmyresume.exception;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class WebExceptionHandler {

    @ExceptionHandler(ResumeNotFoundException.class)
    public String handleResumeNotFoundForPage(ResumeNotFoundException ex, Model model) {
        model.addAttribute("errorMessage", "Currículo não encontrado: " + ex.getMessage());
        return "errorPage"; // Redireciona para a página de erro personalizada
    }
}
