package com.tellingmyresume.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.tellingmyresume.vo.Candidate;
import com.tellingmyresume.vo.Content;
import com.tellingmyresume.vo.GeminiResponseVO;
import com.tellingmyresume.vo.Part;

class GeminiServiceTest {
	
	@Mock
	private RestTemplate restTemplate;
	
	@InjectMocks
	private GeminiService geminiService;
	
	@BeforeEach
	void setUp() {
		org.mockito.MockitoAnnotations.openMocks(this);
	}
	
	@Test
	void generateResumeSuccess() {
	    // Criação de um mock de resposta da API Gemini
	    GeminiResponseVO responseVO = new GeminiResponseVO();
	    Candidate candidate = new Candidate();
	    Content content = new Content();
	    Part part = new Part();
	    part.setText("Resumo gerado pela API Gemini");
	    content.setParts(List.of(part));
	    candidate.setContent(content);
	    responseVO.setCandidates(List.of(candidate));

	    // Criação da ResponseEntity simulada
	    ResponseEntity<GeminiResponseVO> responseEntity = new ResponseEntity<>(responseVO, HttpStatus.OK);

	    // Configura o mock do RestTemplate.exchange para retornar a resposta simulada corretamente
	    when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), eq(GeminiResponseVO.class)))
	            .thenReturn(responseEntity);

	    // Chama o serviço e verifica se o resultado está correto
	    String result = geminiService.generateResume("Resumo original");
	    assertEquals("Resumo gerado pela API Gemini", result);
	}
    
    @Test
    void generateResumeFailure() {
        // Configura o mock do RestTemplate para lançar uma exceção simulada
	    when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), eq(GeminiResponseVO.class)))
        	.thenReturn(any());

        // Verifica se a exceção é lançada corretamente
        Exception exception = assertThrows(RuntimeException.class, () -> {
            geminiService.generateResume("Resumo original");
        });

        // Verifica a mensagem de erro
        assertEquals("Erro ao gerar o currículo na API do Gemini.", exception.getMessage());
    }
	
}
