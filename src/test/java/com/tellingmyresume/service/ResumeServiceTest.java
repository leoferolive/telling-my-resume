package com.tellingmyresume.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import com.tellingmyresume.exception.ResumeNotFoundException;
import com.tellingmyresume.exception.ResumeStorageException;
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
	
	@Test
	void testReadResumeSuccess() throws IOException {
	    String expectedContent = "Conteúdo do currículo em texto";
	    byte[] mockFileContent = expectedContent.getBytes();

	    // Simula a busca pelo arquivo com a extensão .txt
	    when(localStorage.read("test-curriculum.txt")).thenReturn(mockFileContent);
	    
	    // Simula o comportamento da busca pelo arquivo no localStorage com a extensão correta
	    when(localStorage.fileExists("test-curriculum.txt")).thenReturn(true);

	    // Verifica se o conteúdo retornado é o esperado
	    String result = resumeService.readResume("test-curriculum");
	    assertEquals(expectedContent, result);

	    // Verifica se o método read() foi chamado corretamente
	    verify(localStorage, times(1)).read("test-curriculum.txt");
	}


	@Test
	void testReadResumeNotFound() throws IOException {
	    // Simula que nenhum arquivo com o nome e extensões suportadas foi encontrado
	    when(localStorage.read(anyString())).thenThrow(new IOException("Arquivo não encontrado"));

	    // Verifica se a exceção ResumeNotFoundException é lançada
	    Exception exception = assertThrows(ResumeNotFoundException.class, () -> {
	        resumeService.readResume("non-existing-resume");
	    });

	    // Verifica a mensagem de erro da exceção
	    assertEquals("Currículo não encontrado com o nome: non-existing-resume", exception.getMessage());
	}


	@Test
	void testSaveResumeSuccess() throws IOException {
	    // Simula um arquivo multipart para upload
	    MockMultipartFile mockFile = new MockMultipartFile(
	        "file", "new-resume.txt", MediaType.TEXT_PLAIN_VALUE, "Novo conteúdo".getBytes()
	    );

	    // Chama o método de salvar o currículo
	    resumeService.saveResume(mockFile.getOriginalFilename(), mockFile);

	    // Verifica se o método save() foi chamado com os parâmetros corretos
	    verify(localStorage, times(1)).save(mockFile.getOriginalFilename(), mockFile);
	}


	@Test
	void testSaveResumeFailure() throws IOException {
	    // Simula um arquivo multipart para upload
	    MockMultipartFile mockFile = new MockMultipartFile(
	        "file", "problem-resume.txt", MediaType.TEXT_PLAIN_VALUE, "Conteúdo problemático".getBytes()
	    );

	    // Simula uma falha ao salvar o arquivo
	    doThrow(new IOException("Erro ao salvar")).when(localStorage).save(anyString(), any(MultipartFile.class));

	    // Verifica se a exceção ResumeStorageException é lançada
	    Exception exception = assertThrows(ResumeStorageException.class, () -> {
	        resumeService.saveResume(mockFile.getOriginalFilename(), mockFile);
	    });

	    // Verifica a mensagem de erro
	    assertEquals("Erro ao salvar o currículo: " + mockFile.getOriginalFilename(), exception.getMessage());
	}

	
}
