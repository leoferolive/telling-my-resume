package com.tellingmyresume.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.tellingmyresume.exception.GeminiServiceException;
import com.tellingmyresume.formatter.ResumeFormatter;
import com.tellingmyresume.vo.Candidate;
import com.tellingmyresume.vo.GeminiResponseVO;

import io.github.resilience4j.retry.annotation.Retry;

@Service
public class GeminiService {

    private static final Logger LOGGER = LoggerFactory.getLogger(GeminiService.class);

    private static final String GEMINI_URL_TEMPLATE = 
        "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash-latest:generateContent?key=";

    @Value("${api.gemini.key}")
    private String apiKey;

    private final RestTemplate restTemplate;

    public GeminiService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Gera um currículo formatado como JSON com caracteres especiais limpos.
     * 
     * @param resumeContent O conteúdo do currículo a ser processado.
     * @return O currículo formatado como JSON, com caracteres especiais limpos.
     */
    public String generateResumeAsJson(String resumeContent) {
        return ResumeFormatter.cleanSpecialCharacters(generateResume(resumeContent));
    }

    /**
     * Gera o resumo do currículo formatado através da API Gemini.
     * Adicionamos lógica de Retry para tentar várias vezes em caso de falhas.
     * 
     * @param resumeContent O conteúdo do currículo.
     * @return O resumo do currículo processado pela API Gemini.
     */
    @Retry(name = "geminiService", fallbackMethod = "fallbackGenerateResume")
    public String generateResume(String resumeContent) {
        String url = GEMINI_URL_TEMPLATE + apiKey;

        HttpEntity<Map<String, Object>> entity = createHttpEntity(resumeContent);

        try {
            ResponseEntity<GeminiResponseVO> response = restTemplate.exchange(url, HttpMethod.POST, entity, GeminiResponseVO.class);

            return extractCandidateText(response)
                    .orElseThrow(() -> new GeminiServiceException("A resposta da API do Gemini não contém dados suficientes."));

        } catch (GeminiServiceException e) {
            LOGGER.error("Erro específico da API Gemini: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            LOGGER.error("Erro inesperado ao chamar a API do Gemini: {}", e.getMessage(), e);
            throw new GeminiServiceException("Erro ao gerar o currículo na API do Gemini.", e);
        }
    }

    /**
     * Método de fallback que será chamado se todas as tentativas de retry falharem.
     * 
     * @param resumeContent O conteúdo do currículo.
     * @param ex A exceção que causou a falha.
     * @return Uma mensagem de fallback padrão ou uma ação alternativa.
     */
    public String fallbackGenerateResume(String resumeContent, Exception ex) {
        LOGGER.error("Falha ao gerar currículo pela API do Gemini. Executando fallback. Motivo: {}", ex.getMessage());
        return "Não foi possível gerar o resumo no momento. Tente novamente mais tarde.";
    }

    /**
     * Cria o corpo da requisição HTTP, incluindo os cabeçalhos e o conteúdo do currículo.
     * 
     * @param resumeContent O conteúdo do currículo.
     * @return O HttpEntity configurado com o corpo da requisição.
     */
    private HttpEntity<Map<String, Object>> createHttpEntity(String resumeContent) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("contents", List.of(
            Map.of("parts", List.of(
                Map.of("text", "Descreva o currículo a seguir para uma oferta de trabalho de maneira a valorizar o mesmo: " + resumeContent)
            ))
        ));

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");

        return new HttpEntity<>(requestBody, headers);
    }

    /**
     * Extrai o texto do candidato da resposta da API do Gemini.
     * 
     * @param response A resposta da API do Gemini.
     * @return O texto do candidato, se presente, encapsulado em um Optional.
     */
    private Optional<String> extractCandidateText(ResponseEntity<GeminiResponseVO> response) {
        return Optional.ofNullable(response)
                .map(ResponseEntity::getBody)
                .flatMap(body -> body.getCandidates().stream().findFirst())
                .map(Candidate::getContent)
                .flatMap(content -> content.getParts().stream().findFirst())
                .map(part -> part.getText());
    }
}
