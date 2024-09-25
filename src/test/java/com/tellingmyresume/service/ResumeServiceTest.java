package com.tellingmyresume.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import com.tellingmyresume.exception.ResumeNotFoundException;
import com.tellingmyresume.storage.LocalStorage;

class ResumeServiceTest {
	
	@Mock
	private LocalStorage localStorage;
	
	@InjectMocks
	private ResumeService resumeService;
	
	@BeforeEach
	void setUp() {
		org.mockito.MockitoAnnotations.openMocks(this);
	}
	
	/*
	 * @Test void testReadResumeSuccess() throws IOException { String content =
	 * "Conteúdo do currículo";
	 * when(localStorage.read("test-curriculum")).thenReturn(content);
	 * assertEquals(content, resumeService.readResume("test-curriculum")); }
	 */
	
	@Test
	void testReadResumeNotFound() throws IOException {
        // Simula uma exceção quando o arquivo não é encontrado
        when(localStorage.read("non-existing-resume")).thenThrow(new IOException("Arquivo não encontrado"));

        // Verifica se a exceção ResumeNotFoundException é lançada
        Exception exception = assertThrows(ResumeNotFoundException.class, () -> {
            resumeService.readResume("non-existing-resume");
        });

        // Verifica a mensagem de erro da exceção
        assertEquals("Currículo não encontrado: non-existing-resume", exception.getMessage());
	}
	
//    @Test
//    void testSaveResumeSuccess() throws IOException {
//        // Simula o comportamento de salvar o currículo
//        String resumeContent = "Novo currículo";
//        
//        // Chama o método de salvar o currículo
//        resumeService.saveResume("new-resume", resumeContent);
//        
//        // Verifica se o método save() foi chamado com os parâmetros corretos
//        verify(localStorage, times(1)).save("new-resume", resumeContent);
//    }
    
//    @Test
//    void testSaveResumeFailure() throws IOException {
//        // Simula uma falha ao salvar o currículo
//        String resumeContent = "Currículo com problema";
//        doThrow(new IOException("Erro ao salvar")).when(localStorage).save("problem-resume", resumeContent);
//
//        // Verifica se a exceção ResumeStorageException é lançada
//        Exception exception = assertThrows(ResumeStorageException.class, () -> {
//            resumeService.saveResume("problem-resume", resumeContent);
//        });
//
//        // Verifica a mensagem de erro
//        assertEquals("Erro ao salvar o currículo: problem-resume", exception.getMessage());
//    }
//	
}
