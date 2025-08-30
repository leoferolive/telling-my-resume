package com.tellingmyresume.constants;

public final class ApiConstants {
    
    private ApiConstants() {
        // Utility class
    }
    
    // Gemini API
    public static final String GEMINI_BASE_URL = 
        "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash-latest:generateContent?key=";
        
    public static final String GEMINI_PROMPT_TEMPLATE = 
        "Descreva o currículo a seguir para uma oferta de trabalho de maneira a valorizar o mesmo: ";
}