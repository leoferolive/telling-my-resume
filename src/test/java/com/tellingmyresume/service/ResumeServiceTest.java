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
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import com.tellingmyresume.exception.ResumeNotFoundException;
import com.tellingmyresume.exception.ResumeStorageException;
import com.tellingmyresume.util.FileUtils;

@ExtendWith(MockitoExtension.class)
class ResumeServiceTest {

	@Mock
	private DatabaseStorageService storageService;

	@Mock
	private FileUtils fileUtils;

	@InjectMocks
	private ResumeService resumeService;

	
	@Test
	void testReadResumeSuccess() throws IOException {
	    String fileName = "test-curriculum.txt";
	    String expectedContent = "Conteúdo do currículo em texto";
	    byte[] mockFileContent = expectedContent.getBytes();

	    // Simula a leitura do arquivo do storage
	    when(storageService.read(fileName)).thenReturn(mockFileContent);
	    
	    // Simula a extração de texto pelo FileUtils
	    when(fileUtils.extractText(fileName, mockFileContent)).thenReturn(expectedContent);

	    // Verifica se o conteúdo retornado é o esperado
	    String result = resumeService.readResume(fileName);
	    assertEquals(expectedContent, result);

	    // Verifica se os métodos foram chamados corretamente
	    verify(storageService, times(1)).read(fileName);
	    verify(fileUtils, times(1)).extractText(fileName, mockFileContent);
	}


	@Test
	void testReadResumeNotFound() throws IOException {
	    String fileName = "non-existing-resume.txt";
	    
	    // Simula que o arquivo não foi encontrado no storage
	    when(storageService.read(fileName)).thenThrow(new IOException("Arquivo não encontrado"));

	    // Verifica se a exceção ResumeNotFoundException é lançada
	    Exception exception = assertThrows(ResumeNotFoundException.class, () -> {
	        resumeService.readResume(fileName);
	    });

	    // Verifica a mensagem de erro da exceção
	    assertEquals("Falha ao ler o arquivo: Arquivo não encontrado", exception.getMessage());
	}


	@Test
	void testSaveResumeSuccess() throws IOException {
	    // Simula um arquivo multipart para upload
	    MockMultipartFile mockFile = new MockMultipartFile(
	        "file", "new-resume.txt", MediaType.TEXT_PLAIN_VALUE, "Novo conteúdo".getBytes()
	    );
	    String fileName = mockFile.getOriginalFilename();

	    // Chama o método de salvar o currículo
	    resumeService.saveResume(fileName, mockFile);

	    // Verifica se o método save() foi chamado com os parâmetros corretos
	    verify(storageService, times(1)).save(fileName, mockFile);
	}


	@Test
	void testSaveResumeFailure() throws IOException {
	    // Simula um arquivo multipart para upload
	    MockMultipartFile mockFile = new MockMultipartFile(
	        "file", "problem-resume.txt", MediaType.TEXT_PLAIN_VALUE, "Conteúdo problemático".getBytes()
	    );
	    String fileName = mockFile.getOriginalFilename();

	    // Simula uma falha ao salvar o arquivo
	    doThrow(new IOException("Erro ao salvar")).when(storageService).save(fileName, mockFile);

	    // Verifica se a exceção ResumeStorageException é lançada
	    Exception exception = assertThrows(ResumeStorageException.class, () -> {
	        resumeService.saveResume(fileName, mockFile);
	    });

	    // Verifica a mensagem de erro
	    assertEquals("Falha ao salvar o arquivo: Erro ao salvar", exception.getMessage());
	}

	
}
