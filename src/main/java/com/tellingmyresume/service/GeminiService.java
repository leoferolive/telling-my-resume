package com.tellingmyresume.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.tellingmyresume.constants.ApiConstants;
import com.tellingmyresume.constants.ErrorMessages;
import com.tellingmyresume.exception.GeminiServiceException;
import com.tellingmyresume.dto.response.Candidate;
import com.tellingmyresume.dto.response.GeminiResponseVO;

import io.github.resilience4j.retry.annotation.Retry;

@Service
public class GeminiService implements AIAnalysisService {

    private static final Logger LOGGER = LoggerFactory.getLogger(GeminiService.class);

    @Value("${api.gemini.key}")
    private String apiKey;

    private final RestTemplate restTemplate;

    public GeminiService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Gera o resumo do currículo formatado através da API Gemini.
     * Adicionamos lógica de Retry para tentar várias vezes em caso de falhas.
     * 
     * @param resumeContent O conteúdo do currículo.
     * @return O resumo do currículo processado pela API Gemini.
     */
    @Override
    @Retry(name = "geminiService", fallbackMethod = "fallbackGenerateResume")
    public String generateResume(String resumeContent) throws GeminiServiceException {
        if (resumeContent == null || resumeContent.trim().isEmpty()) {
            throw new GeminiServiceException("Conteúdo do currículo não pode estar vazio");
        }
        String url = ApiConstants.GEMINI_BASE_URL + apiKey;

        HttpEntity<Map<String, Object>> entity = createHttpEntity(resumeContent);

        try {
            ResponseEntity<GeminiResponseVO> response = restTemplate.exchange(url, HttpMethod.POST, entity, GeminiResponseVO.class);

            return extractCandidateText(response)
                    .orElseThrow(() -> new GeminiServiceException(ErrorMessages.GEMINI_INSUFFICIENT_DATA));

        } catch (GeminiServiceException e) {
            LOGGER.error("Erro específico da API Gemini: {}", e.getMessage());
            throw e;
        } catch (RestClientException e) {
            LOGGER.error("Erro de conectividade com a API do Gemini: {}", e.getMessage(), e);
            throw new GeminiServiceException("Erro de conectividade com o serviço Gemini: " + e.getMessage(), e);
        } catch (Exception e) {
            LOGGER.error("Erro inesperado ao chamar a API do Gemini: {}", e.getMessage(), e);
            throw new GeminiServiceException(ErrorMessages.GEMINI_API_ERROR, e);
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
        return ErrorMessages.GEMINI_FALLBACK_MESSAGE;
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
                Map.of("text", ApiConstants.GEMINI_PROMPT_TEMPLATE + resumeContent)
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

    @Override
    @Async
    public CompletableFuture<String> generateResumeAsync(String resumeContent) {
        try {
            String result = generateResume(resumeContent);
            return CompletableFuture.completedFuture(result);
        } catch (Exception e) {
            CompletableFuture<String> future = new CompletableFuture<>();
            future.completeExceptionally(e);
            return future;
        }
    }

    @Override
    public String getProviderName() {
        return "Gemini";
    }

    @Override
    public boolean isServiceAvailable() {
        try {
            return restTemplate != null && apiKey != null && !apiKey.equals("demo-key");
        } catch (Exception e) {
            LOGGER.warn("Falha ao verificar disponibilidade do serviço Gemini: {}", e.getMessage());
            return false;
        }
    }
}
