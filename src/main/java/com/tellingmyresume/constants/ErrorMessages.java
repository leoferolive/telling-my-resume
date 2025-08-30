package com.tellingmyresume.constants;

public final class ErrorMessages {
    
    private ErrorMessages() {
        // Utility class
    }
    
    // Generic errors
    public static final String UNEXPECTED_ERROR = "Ocorreu um erro inesperado no sistema.";
    public static final String REQUEST_PROCESSING_ERROR = "Erro ao processar a requisição: ";
    
    // Resume errors
    public static final String RESUME_NOT_FOUND = "Currículo não encontrado: ";
    public static final String RESUME_SAVE_ERROR = "Erro ao salvar o arquivo: ";
    public static final String RESUME_INVALID = "Currículo inválido: ";
    public static final String RESUME_GENERATION_ERROR = "Erro ao gerar o currículo: ";
    
    // Gemini API errors
    public static final String GEMINI_API_ERROR = "Erro ao gerar o currículo na API do Gemini.";
    public static final String GEMINI_INSUFFICIENT_DATA = "A resposta da API do Gemini não contém dados suficientes.";
    public static final String GEMINI_FALLBACK_MESSAGE = "Não foi possível gerar o resumo no momento. Tente novamente mais tarde.";
    
    // Claude API errors
    public static final String CLAUDE_GENERATION_ERROR = "Erro ao gerar resumo com Claude";
    public static final String CLAUDE_API_ERROR = "Erro ao gerar o resumo com Claude: ";
    
    // File processing errors
    public static final String PDF_PROCESSING_ERROR = "Erro ao processar arquivo PDF: ";
    public static final String DOCX_PROCESSING_ERROR = "Erro ao processar arquivo DOCX: ";
    public static final String FILE_READ_ERROR = "Falha ao ler o arquivo: ";
    public static final String FILE_SAVE_ERROR = "Falha ao salvar o arquivo: ";
    
    // Database errors
    public static final String DATABASE_FILE_EMPTY = "Arquivo está vazio";
    public static final String DATABASE_FILE_NOT_FOUND = "Arquivo não encontrado: ";
    public static final String DATABASE_FILE_EXISTS = "Arquivo já existe: ";
}