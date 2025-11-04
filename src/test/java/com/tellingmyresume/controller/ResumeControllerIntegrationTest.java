package com.tellingmyresume.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import com.tellingmyresume.dto.request.ResumeUploadRequest;
import com.tellingmyresume.dto.response.ResumeAnalysisResponse;
import com.tellingmyresume.dto.response.ResumeContentResponse;
import com.tellingmyresume.dto.response.ResumeUploadResponse;
import com.tellingmyresume.service.ResumeAnalysisService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@WebMvcTest(ResumeController.class)
class ResumeControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ResumeAnalysisService resumeAnalysisService;

    @MockBean
    private com.tellingmyresume.interceptor.RateLimitingInterceptor rateLimitingInterceptor;

    @MockBean
    private com.tellingmyresume.config.CorrelationIdInterceptor correlationIdInterceptor;

    @Test
    void testUploadResume_Success() throws Exception {
        // Arrange
        String fileName = "test-resume.pdf";
        ResumeUploadResponse expectedResponse = new ResumeUploadResponse(fileName, "Upload successful", 1024L);
        MockMultipartFile file = new MockMultipartFile("file", fileName, "application/pdf", "test content".getBytes());
        
        when(resumeAnalysisService.uploadResume(any(ResumeUploadRequest.class))).thenReturn(expectedResponse);

        // Act & Assert
        mockMvc.perform(multipart("/resume/upload")
                .file(file)
                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fileName").value(fileName))
                .andExpect(jsonPath("$.message").value("Upload successful"));
    }

    @Test
    void testReadResume_Success() throws Exception {
        // Arrange
        String fileName = "test-resume.pdf";
        String content = "Resume content here";
        ResumeContentResponse expectedResponse = new ResumeContentResponse();
        expectedResponse.setFileName(fileName);
        expectedResponse.setContent(content);
        expectedResponse.setContentType("text/plain");
        
        when(resumeAnalysisService.getResumeContent(fileName)).thenReturn(expectedResponse);

        // Act & Assert
        mockMvc.perform(get("/resume/read/{fileName}", fileName))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fileName").value(fileName))
                .andExpect(jsonPath("$.content").value(content))
                .andExpect(jsonPath("$.contentType").value("text/plain"));
    }

    @Test
    void testAnalyzeResumeWithBestProvider_Success() throws Exception {
        // Arrange
        String fileName = "test-resume.pdf";
        String analysis = "AI analysis result";
        ResumeAnalysisResponse expectedResponse = new ResumeAnalysisResponse();
        expectedResponse.setFileName(fileName);
        expectedResponse.setAnalysis(analysis);
        expectedResponse.setAiProvider("Claude");
        
        when(resumeAnalysisService.analyzeResumeWithBestAvailable(fileName)).thenReturn(expectedResponse);

        // Act & Assert
        mockMvc.perform(get("/resume/analyze/{fileName}", fileName))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fileName").value(fileName))
                .andExpect(jsonPath("$.analysis").value(analysis))
                .andExpect(jsonPath("$.aiProvider").value("Claude"));
    }

    @Test
    void testGenerateResumeWithClaude_Success() throws Exception {
        // Arrange
        String fileName = "test-resume.pdf";
        String analysis = "Claude analysis result";
        ResumeAnalysisResponse expectedResponse = new ResumeAnalysisResponse();
        expectedResponse.setFileName(fileName);
        expectedResponse.setAnalysis(analysis);
        expectedResponse.setAiProvider("Claude");
        
        when(resumeAnalysisService.analyzeResumeWithProvider(fileName, "Claude")).thenReturn(expectedResponse);

        // Act & Assert
        mockMvc.perform(get("/resume/generateClaude/{fileName}", fileName))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fileName").value(fileName))
                .andExpect(jsonPath("$.analysis").value(analysis))
                .andExpect(jsonPath("$.aiProvider").value("Claude"));
    }

    @Test
    void testGenerateResumeWithGemini_Success() throws Exception {
        // Arrange
        String fileName = "test-resume.pdf";
        String analysis = "Gemini analysis result";
        ResumeAnalysisResponse expectedResponse = new ResumeAnalysisResponse();
        expectedResponse.setFileName(fileName);
        expectedResponse.setAnalysis(analysis);
        expectedResponse.setAiProvider("Gemini");
        
        when(resumeAnalysisService.analyzeResumeWithProvider(fileName, "Gemini")).thenReturn(expectedResponse);

        // Act & Assert
        mockMvc.perform(get("/resume/generate/{fileName}", fileName))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fileName").value(fileName))
                .andExpect(jsonPath("$.analysis").value(analysis))
                .andExpect(jsonPath("$.aiProvider").value("Gemini"));
    }

    @Test
    void testReadResume_NotFound() throws Exception {
        // Arrange
        String fileName = "non-existent.pdf";
        
        when(resumeAnalysisService.getResumeContent(fileName))
            .thenThrow(new com.tellingmyresume.exception.ResumeNotFoundException("File not found"));

        // Act & Assert
        mockMvc.perform(get("/resume/read/{fileName}", fileName))
                .andExpect(status().isNotFound());
    }
}