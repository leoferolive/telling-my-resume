package com.tellingmyresume.controller;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.tellingmyresume.exception.GlobalExceptionHandler;
import com.tellingmyresume.service.GeminiService;
import com.tellingmyresume.service.ResumeService;

@ExtendWith(MockitoExtension.class)
class ResumeControllerTest {
	
	private MockMvc mockMvc;
	
	@Mock
	private ResumeService resumeService;
	
	@Mock
	private GeminiService geminiService;
	
	@InjectMocks
	private ResumeController resumeController;
	
	@BeforeEach
	void setUp() {
		mockMvc = MockMvcBuilders.standaloneSetup(resumeController)
				.setControllerAdvice(new GlobalExceptionHandler())
				.build();
	}
	
	@Test
	void testGenerateResumeSuccess() throws Exception {
		when(resumeService.readResume(anyString())).thenReturn("Conteúdo do currículo");
		when(geminiService.generateResume(anyString())).thenReturn("Currículo gerado");
		
        mockMvc.perform(get("/resume/generate/test-curriculum")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Currículo gerado"));
	}
	
	@Test
	void testGenerateResumeNotFound() throws Exception {
	    // Simula uma exceção ao tentar ler um currículo que não existe
	    when(resumeService.readResume(anyString())).thenThrow(new RuntimeException("Currículo não encontrado"));

	    // Executa a requisição HTTP GET e espera uma resposta de erro 500
	    mockMvc.perform(get("/resume/generate/test-curriculum")
	            .contentType(MediaType.APPLICATION_JSON))
	            .andExpect(status().isInternalServerError())
	            .andExpect(content().string("Erro ao gerar o currículo: Currículo não encontrado"));
	}
	
}
