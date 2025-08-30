package com.tellingmyresume.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.concurrent.CompletableFuture;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.web.multipart.MultipartFile;

import com.tellingmyresume.dto.request.ResumeUploadRequest;
import com.tellingmyresume.dto.response.ResumeAnalysisResponse;
import com.tellingmyresume.dto.response.ResumeContentResponse;
import com.tellingmyresume.dto.response.ResumeUploadResponse;
import com.tellingmyresume.exception.AIServiceException;
import com.tellingmyresume.exception.ResumeNotFoundException;
import com.tellingmyresume.exception.ResumeStorageException;
import com.tellingmyresume.mapper.ResumeMapperInterface;
import com.tellingmyresume.service.ResumeDataService;
import com.tellingmyresume.service.impl.ResumeAnalysisServiceImpl;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ResumeAnalysisServiceTest {

    @Mock
    private ResumeDataService resumeService;
    
    @Mock
    private AIAnalysisService claudeService;
    
    @Mock
    private AIAnalysisService geminiService;
    
    @Mock
    private ResumeMapperInterface resumeMapper;
    
    @Mock
    private MultipartFile mockFile;

    private ResumeAnalysisService resumeAnalysisService;

    @BeforeEach
    void setUp() {
        resumeAnalysisService = new ResumeAnalysisServiceImpl(
            resumeService, claudeService, geminiService, resumeMapper);
    }

    @Test
    void testUploadResume_Success() throws Exception {
        // Arrange
        String fileName = "test-resume.pdf";
        ResumeUploadResponse expectedResponse = new ResumeUploadResponse(fileName, "Upload successful");
        ResumeUploadRequest uploadRequest = new ResumeUploadRequest();
        uploadRequest.setFile(mockFile);
        
        when(resumeMapper.extractFileName(uploadRequest)).thenReturn(fileName);
        when(resumeMapper.toUploadResponse(fileName, mockFile)).thenReturn(expectedResponse);

        // Act
        ResumeUploadResponse result = resumeAnalysisService.uploadResume(uploadRequest);

        // Assert
        assertNotNull(result);
        assertEquals(fileName, result.getFileName());
        verify(resumeService).saveResume(fileName, mockFile);
        verify(resumeMapper).toUploadResponse(fileName, mockFile);
    }

    @Test
    void testUploadResume_StorageException() throws Exception {
        // Arrange
        String fileName = "test-resume.pdf";
        String errorMessage = "Storage failed";
        ResumeUploadResponse errorResponse = new ResumeUploadResponse(fileName, errorMessage);
        ResumeUploadRequest uploadRequest = new ResumeUploadRequest();
        uploadRequest.setFile(mockFile);
        
        when(resumeMapper.extractFileName(uploadRequest)).thenReturn(fileName);
        doThrow(new ResumeStorageException(errorMessage))
            .when(resumeService).saveResume(fileName, mockFile);
        when(resumeMapper.toUploadErrorResponse(fileName, errorMessage)).thenReturn(errorResponse);

        // Act
        ResumeUploadResponse result = resumeAnalysisService.uploadResume(uploadRequest);

        // Assert
        assertNotNull(result);
        assertEquals(fileName, result.getFileName());
        assertEquals(errorMessage, result.getMessage());
        verify(resumeMapper).toUploadErrorResponse(fileName, errorMessage);
    }

    @Test
    void testGetResumeContent_Success() throws Exception {
        // Arrange
        String fileName = "test-resume.pdf";
        String content = "Resume content here";
        ResumeContentResponse expectedResponse = new ResumeContentResponse();
        expectedResponse.setFileName(fileName);
        expectedResponse.setContent(content);
        
        when(resumeService.readResume(fileName)).thenReturn(content);
        when(resumeMapper.toContentResponse(fileName, content, "text/plain")).thenReturn(expectedResponse);

        // Act
        ResumeContentResponse result = resumeAnalysisService.getResumeContent(fileName);

        // Assert
        assertNotNull(result);
        assertEquals(fileName, result.getFileName());
        assertEquals(content, result.getContent());
        verify(resumeService).readResume(fileName);
        verify(resumeMapper).toContentResponse(fileName, content, "text/plain");
    }

    @Test
    void testAnalyzeResumeWithProvider_Claude_Success() throws Exception {
        // Arrange
        String fileName = "test-resume.pdf";
        String provider = "Claude";
        String resumeContent = "Resume content";
        String analysis = "AI analysis result";
        ResumeAnalysisResponse expectedResponse = new ResumeAnalysisResponse();
        expectedResponse.setFileName(fileName);
        expectedResponse.setAnalysis(analysis);
        expectedResponse.setAiProvider(provider);
        
        when(claudeService.getProviderName()).thenReturn("Claude");
        when(geminiService.getProviderName()).thenReturn("Gemini");
        when(resumeService.readResume(fileName)).thenReturn(resumeContent);
        when(claudeService.isServiceAvailable()).thenReturn(true);
        when(claudeService.generateResume(resumeContent)).thenReturn(analysis);
        when(resumeMapper.toAnalysisResponse(fileName, analysis, provider)).thenReturn(expectedResponse);

        // Act
        ResumeAnalysisResponse result = resumeAnalysisService.analyzeResumeWithProvider(fileName, provider);

        // Assert
        assertNotNull(result);
        assertEquals(fileName, result.getFileName());
        assertEquals(analysis, result.getAnalysis());
        assertEquals(provider, result.getAiProvider());
        verify(claudeService).isServiceAvailable();
        verify(claudeService).generateResume(resumeContent);
    }

    @Test
    void testAnalyzeResumeWithProvider_Gemini_Success() throws Exception {
        // Arrange
        String fileName = "test-resume.pdf";
        String provider = "Gemini";
        String resumeContent = "Resume content";
        String analysis = "AI analysis result";
        ResumeAnalysisResponse expectedResponse = new ResumeAnalysisResponse();
        expectedResponse.setFileName(fileName);
        expectedResponse.setAnalysis(analysis);
        expectedResponse.setAiProvider(provider);
        
        when(claudeService.getProviderName()).thenReturn("Claude");
        when(geminiService.getProviderName()).thenReturn("Gemini");
        when(resumeService.readResume(fileName)).thenReturn(resumeContent);
        when(geminiService.isServiceAvailable()).thenReturn(true);
        when(geminiService.generateResume(resumeContent)).thenReturn(analysis);
        when(resumeMapper.toAnalysisResponse(fileName, analysis, provider)).thenReturn(expectedResponse);

        // Act
        ResumeAnalysisResponse result = resumeAnalysisService.analyzeResumeWithProvider(fileName, provider);

        // Assert
        assertNotNull(result);
        assertEquals(fileName, result.getFileName());
        assertEquals(analysis, result.getAnalysis());
        assertEquals(provider, result.getAiProvider());
        verify(geminiService).isServiceAvailable();
        verify(geminiService).generateResume(resumeContent);
    }

    @Test
    void testAnalyzeResumeWithProvider_ServiceUnavailable() throws Exception {
        // Arrange
        String fileName = "test-resume.pdf";
        String provider = "Claude";
        String resumeContent = "Resume content";
        
        when(claudeService.getProviderName()).thenReturn("Claude");
        when(geminiService.getProviderName()).thenReturn("Gemini");
        when(resumeService.readResume(fileName)).thenReturn(resumeContent);
        when(claudeService.isServiceAvailable()).thenReturn(false);

        // Act & Assert
        assertThrows(AIServiceException.class, () -> 
            resumeAnalysisService.analyzeResumeWithProvider(fileName, provider));
        
        verify(claudeService).isServiceAvailable();
        verify(claudeService, times(0)).generateResume(anyString());
    }

    @Test
    void testAnalyzeResumeWithProvider_UnsupportedProvider() throws Exception {
        // Arrange
        String fileName = "test-resume.pdf";
        String provider = "UnsupportedAI";
        String resumeContent = "Resume content";
        
        when(claudeService.getProviderName()).thenReturn("Claude");
        when(geminiService.getProviderName()).thenReturn("Gemini");
        when(resumeService.readResume(fileName)).thenReturn(resumeContent);

        // Act & Assert
        assertThrows(AIServiceException.class, () -> 
            resumeAnalysisService.analyzeResumeWithProvider(fileName, provider));
    }

    @Test
    void testAnalyzeResumeWithBestAvailable_ClaudeFirst() throws Exception {
        // Arrange
        String fileName = "test-resume.pdf";
        String resumeContent = "Resume content";
        String analysis = "Claude analysis";
        ResumeAnalysisResponse expectedResponse = new ResumeAnalysisResponse();
        expectedResponse.setAnalysis(analysis);
        expectedResponse.setAiProvider("Claude");
        
        when(claudeService.getProviderName()).thenReturn("Claude");
        when(geminiService.getProviderName()).thenReturn("Gemini");
        when(resumeService.readResume(fileName)).thenReturn(resumeContent);
        when(claudeService.isServiceAvailable()).thenReturn(true);
        when(claudeService.generateResume(resumeContent)).thenReturn(analysis);
        when(resumeMapper.toAnalysisResponse(fileName, analysis, "Claude")).thenReturn(expectedResponse);

        // Act
        ResumeAnalysisResponse result = resumeAnalysisService.analyzeResumeWithBestAvailable(fileName);

        // Assert
        assertNotNull(result);
        assertEquals("Claude", result.getAiProvider());
        assertEquals(analysis, result.getAnalysis());
        verify(claudeService, times(2)).isServiceAvailable(); // Called once in loop, once in analyzeResumeWithProvider
        verify(claudeService).generateResume(resumeContent);
        verify(geminiService, times(0)).isServiceAvailable(); // Should not check Gemini if Claude is available
    }

    @Test
    void testAnalyzeResumeWithBestAvailable_FallbackToGemini() throws Exception {
        // Arrange
        String fileName = "test-resume.pdf";
        String resumeContent = "Resume content";
        String analysis = "Gemini analysis";
        ResumeAnalysisResponse expectedResponse = new ResumeAnalysisResponse();
        expectedResponse.setAnalysis(analysis);
        expectedResponse.setAiProvider("Gemini");
        
        when(claudeService.getProviderName()).thenReturn("Claude");
        when(geminiService.getProviderName()).thenReturn("Gemini");
        when(resumeService.readResume(fileName)).thenReturn(resumeContent);
        when(claudeService.isServiceAvailable()).thenReturn(false);
        when(geminiService.isServiceAvailable()).thenReturn(true);
        when(geminiService.generateResume(resumeContent)).thenReturn(analysis);
        when(resumeMapper.toAnalysisResponse(fileName, analysis, "Gemini")).thenReturn(expectedResponse);

        // Act
        ResumeAnalysisResponse result = resumeAnalysisService.analyzeResumeWithBestAvailable(fileName);

        // Assert
        assertNotNull(result);
        assertEquals("Gemini", result.getAiProvider());
        assertEquals(analysis, result.getAnalysis());
        verify(claudeService).isServiceAvailable();
        verify(geminiService, times(2)).isServiceAvailable(); // Called once in loop, once in analyzeResumeWithProvider
        verify(geminiService).generateResume(resumeContent);
    }

    @Test
    void testAnalyzeResumeWithBestAvailable_NoServicesAvailable() throws Exception {
        // Arrange
        String fileName = "test-resume.pdf";
        
        when(claudeService.isServiceAvailable()).thenReturn(false);
        when(geminiService.isServiceAvailable()).thenReturn(false);

        // Act & Assert
        assertThrows(AIServiceException.class, () -> 
            resumeAnalysisService.analyzeResumeWithBestAvailable(fileName));
        
        verify(claudeService).isServiceAvailable();
        verify(geminiService).isServiceAvailable();
    }

    @Test
    void testAnalyzeResumeAsync_Success() throws Exception {
        // Arrange
        String fileName = "test-resume.pdf";
        String provider = "Claude";
        String resumeContent = "Resume content";
        String analysis = "AI analysis result";
        ResumeAnalysisResponse expectedResponse = new ResumeAnalysisResponse();
        expectedResponse.setFileName(fileName);
        expectedResponse.setAnalysis(analysis);
        expectedResponse.setAiProvider(provider);
        
        when(claudeService.getProviderName()).thenReturn("Claude");
        when(geminiService.getProviderName()).thenReturn("Gemini");
        when(resumeService.readResume(fileName)).thenReturn(resumeContent);
        when(claudeService.isServiceAvailable()).thenReturn(true);
        when(claudeService.generateResume(resumeContent)).thenReturn(analysis);
        when(resumeMapper.toAnalysisResponse(fileName, analysis, provider)).thenReturn(expectedResponse);

        // Act
        CompletableFuture<ResumeAnalysisResponse> result = resumeAnalysisService.analyzeResumeAsync(fileName, provider);

        // Assert
        assertNotNull(result);
        ResumeAnalysisResponse response = result.get(); // This will block, but it's a test
        assertEquals(fileName, response.getFileName());
        assertEquals(analysis, response.getAnalysis());
        assertEquals(provider, response.getAiProvider());
    }

    @Test
    void testIsResumeAvailable_True() {
        // Arrange
        String fileName = "test-resume.pdf";
        when(resumeService.resumeExists(fileName)).thenReturn(true);

        // Act
        boolean result = resumeAnalysisService.isResumeAvailable(fileName);

        // Assert
        assertTrue(result);
        verify(resumeService).resumeExists(fileName);
    }

    @Test
    void testIsResumeAvailable_False() {
        // Arrange
        String fileName = "non-existent.pdf";
        when(resumeService.resumeExists(fileName)).thenReturn(false);

        // Act
        boolean result = resumeAnalysisService.isResumeAvailable(fileName);

        // Assert
        assertFalse(result);
        verify(resumeService).resumeExists(fileName);
    }

    @Test
    void testDeleteResume_Success() throws Exception {
        // Arrange
        String fileName = "test-resume.pdf";

        // Act
        resumeAnalysisService.deleteResume(fileName);

        // Assert
        verify(resumeService).deleteResume(fileName);
    }

    @Test
    void testDeleteResume_ResumeNotFound() throws Exception {
        // Arrange
        String fileName = "non-existent.pdf";
        doThrow(new ResumeNotFoundException("File not found"))
            .when(resumeService).deleteResume(fileName);

        // Act & Assert
        assertThrows(ResumeNotFoundException.class, () -> 
            resumeAnalysisService.deleteResume(fileName));
        
        verify(resumeService).deleteResume(fileName);
    }
}