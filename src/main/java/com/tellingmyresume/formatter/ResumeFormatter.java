package com.tellingmyresume.formatter;

import org.springframework.stereotype.Service;

@Service
public class ResumeFormatter {

    /**
     * Formata o currículo como uma string com quebras de linha e títulos.
     * @param resumeContent O conteúdo do currículo a ser formatado.
     * @return O currículo formatado como texto.
     */
    public static String formatResume(String resumeContent) {
        // Aqui você pode adicionar mais lógicas de formatação conforme necessário
        return resumeContent.replace("\n", "<br>")
                            .replace("**", "<strong>")
                            .replace("* ", "<li>");
    }

    /**
     * Limpa caracteres especiais do conteúdo do currículo.
     * @param resumeContent O conteúdo do currículo a ser formatado.
     * @return O conteúdo do currículo com caracteres especiais limpos.
     */
    public static String cleanSpecialCharacters(String resumeContent) {
        // Substitui quebras de linha e caracteres especiais
        return resumeContent.replaceAll("[\\n\\r]+", " ")
                            .replaceAll("[^\\x20-\\x7E]", ""); // Remove caracteres não-ASCII, por exemplo
    }
}

