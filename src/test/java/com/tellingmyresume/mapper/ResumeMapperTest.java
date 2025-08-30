package com.tellingmyresume.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import com.tellingmyresume.dto.request.ResumeUploadRequest;
import com.tellingmyresume.dto.response.ResumeAnalysisResponse;
import com.tellingmyresume.dto.response.ResumeContentResponse;
import com.tellingmyresume.dto.response.ResumeUploadResponse;

@ExtendWith(MockitoExtension.class)
class ResumeMapperTest {

    @InjectMocks
    private ResumeMapper resumeMapper;

    @Mock
    private MultipartFile mockFile;

    @Test
    void testToUploadResponse() {
        // Arrange
        String fileName = "test_resume.pdf";
        when(mockFile.getSize()).thenReturn(1024L);

        // Act
        ResumeUploadResponse response = resumeMapper.toUploadResponse(fileName, mockFile);

        // Assert
        assertNotNull(response);
        assertEquals(fileName, response.getFileName());
        assertEquals("Arquivo enviado com sucesso!", response.getMessage());
        assertEquals(1024L, response.getFileSize());
        assertNotNull(response.getUploadedAt());
    }

    @Test
    void testToUploadErrorResponse() {
        // Arrange
        String fileName = "test_resume.pdf";
        String errorMessage = "Erro ao salvar";

        // Act
        ResumeUploadResponse response = resumeMapper.toUploadErrorResponse(fileName, errorMessage);

        // Assert
        assertNotNull(response);
        assertEquals(fileName, response.getFileName());
        assertEquals(errorMessage, response.getMessage());
    }

    @Test
    void testToContentResponse() {
        // Arrange
        String fileName = "test_resume.pdf";
        String content = "Resume content";
        String contentType = "text/plain";

        // Act
        ResumeContentResponse response = resumeMapper.toContentResponse(fileName, content, contentType);

        // Assert
        assertNotNull(response);
        assertEquals(fileName, response.getFileName());
        assertEquals(content, response.getContent());
        assertEquals(contentType, response.getContentType());
        assertEquals((long) content.length(), response.getContentLength());
        assertNotNull(response.getRetrievedAt());
    }

    @Test
    void testToAnalysisResponse() {
        // Arrange
        String fileName = "test_resume.pdf";
        String analysis = "Analysis result";
        String aiProvider = "Gemini";

        // Act
        ResumeAnalysisResponse response = resumeMapper.toAnalysisResponse(fileName, analysis, aiProvider);

        // Assert
        assertNotNull(response);
        assertEquals(fileName, response.getFileName());
        assertEquals(analysis, response.getAnalysis());
        assertEquals(aiProvider, response.getAiProvider());
        assertTrue(response.isSuccess());
        assertNotNull(response.getAnalyzedAt());
    }

    @Test
    void testToAnalysisErrorResponse() {
        // Arrange
        String fileName = "test_resume.pdf";
        String errorMessage = "Analysis failed";
        String aiProvider = "Gemini";

        // Act
        ResumeAnalysisResponse response = resumeMapper.toAnalysisErrorResponse(fileName, errorMessage, aiProvider);

        // Assert
        assertNotNull(response);
        assertEquals(fileName, response.getFileName());
        assertEquals(errorMessage, response.getAnalysis());
        assertEquals(aiProvider, response.getAiProvider());
        assertFalse(response.isSuccess());
        assertNotNull(response.getAnalyzedAt());
    }

    @Test
    void testExtractFileName_WithCustomFileName() {
        // Arrange
        ResumeUploadRequest request = new ResumeUploadRequest(mockFile);
        request.setCustomFileName("custom_name.pdf");

        // Act
        String fileName = resumeMapper.extractFileName(request);

        // Assert
        assertEquals("custom_name.pdf", fileName);
    }

    @Test
    void testExtractFileName_WithoutCustomFileName() {
        // Arrange
        when(mockFile.getOriginalFilename()).thenReturn("original.pdf");
        ResumeUploadRequest request = new ResumeUploadRequest(mockFile);

        // Act
        String fileName = resumeMapper.extractFileName(request);

        // Assert
        assertEquals("original.pdf", fileName);
    }
}