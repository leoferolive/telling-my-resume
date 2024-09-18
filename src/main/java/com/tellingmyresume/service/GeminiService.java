package com.tellingmyresume.service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.tellingmyresume.vo.Candidate;
import com.tellingmyresume.vo.GeminiResponseVO;

@Service
public class GeminiService {

	private static final Logger LOGGER = LoggerFactory.getLogger(GeminiService.class);
	
	private final RestTemplate restTemplate;

	@Value("${api.gemini.key}")
    private String apiKey;
    
    public GeminiService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String generateResume(String resumeContent) {
        String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash-latest:generateContent?key=" + apiKey;

        // Criando o corpo da requisição
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("contents", List.of(
            Map.of("parts", List.of(
                Map.of("text", "resuma o currículo a seguir: " + resumeContent)
            ))
        ));

        // Configurando os cabeçalhos
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");

        // Enviando a requisição com o corpo e cabeçalhos
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
        
        try {
            // Executando a requisição POST e mapeando diretamente para a classe VO
            ResponseEntity<GeminiResponseVO> response = restTemplate.exchange(url, HttpMethod.POST, entity, GeminiResponseVO.class);

            // Verificando a resposta e navegando até o texto gerado
            GeminiResponseVO responseBody = response.getBody();
            if (responseBody != null && !responseBody.getCandidates().isEmpty()) {
                Candidate candidate = responseBody.getCandidates().get(0);
                if (candidate.getContent() != null && !candidate.getContent().getParts().isEmpty()) {
                    String generatedText = candidate.getContent().getParts().get(0).getText();
                    return generatedText;
                }
            }
            throw new GeminiServiceException("A resposta da API do Gemini não contém dados suficientes.");
            
        } catch (GeminiServiceException e) {
            throw e;  // Repassa exceções específicas da API do Gemini
        } catch (Exception e) {
            // Encapsula qualquer outra exceção como uma GeminiServiceException
        	LOGGER.error("Error ao gerar resposta: {}", Arrays.toString(e.getStackTrace()));
        	throw new GeminiServiceException("Erro ao gerar o currículo na API do Gemini.", e);
        }
    }
}