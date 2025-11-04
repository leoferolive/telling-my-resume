package com.tellingmyresume.controller.v1;

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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@WebMvcTest(
    controllers = ResumeControllerV1.class,
    excludeAutoConfiguration = {com.tellingmyresume.config.WebConfig.class}
)
class ResumeControllerV1IntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ResumeAnalysisService resumeAnalysisService;

    @Test
    void testUploadResume_V1_Success() throws Exception {
        // Arrange
        String fileName = "test-resume.pdf";
        ResumeUploadResponse expectedResponse = new ResumeUploadResponse(fileName, "Upload successful", 1024L);
        MockMultipartFile file = new MockMultipartFile("file", fileName, "application/pdf", "test content".getBytes());
        
        when(resumeAnalysisService.uploadResume(any(ResumeUploadRequest.class))).thenReturn(expectedResponse);

        // Act & Assert
        mockMvc.perform(multipart("/api/v1/resume/upload")
                .file(file)
                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fileName").value(fileName))
                .andExpect(jsonPath("$.message").value("Upload successful"))
                .andExpect(jsonPath("$.fileSize").value(1024));
    }

    @Test
    void testGetResumeContent_V1_Success() throws Exception {
        // Arrange
        String fileName = "test-resume.pdf";
        String content = "Resume content here";
        ResumeContentResponse expectedResponse = new ResumeContentResponse();
        expectedResponse.setFileName(fileName);
        expectedResponse.setContent(content);
        expectedResponse.setContentType("text/plain");
        
        when(resumeAnalysisService.getResumeContent(fileName)).thenReturn(expectedResponse);

        // Act & Assert
        mockMvc.perform(get("/api/v1/resume/content/{fileName}", fileName))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fileName").value(fileName))
                .andExpect(jsonPath("$.content").value(content))
                .andExpect(jsonPath("$.contentType").value("text/plain"));
    }

    @Test
    void testAnalyzeResume_V1_Success() throws Exception {
        // Arrange
        String fileName = "test-resume.pdf";
        String analysis = "Smart AI analysis result";
        ResumeAnalysisResponse expectedResponse = new ResumeAnalysisResponse();
        expectedResponse.setFileName(fileName);
        expectedResponse.setAnalysis(analysis);
        expectedResponse.setAiProvider("Claude");
        
        when(resumeAnalysisService.analyzeResumeWithBestAvailable(fileName)).thenReturn(expectedResponse);

        // Act & Assert
        mockMvc.perform(get("/api/v1/resume/analyze/{fileName}", fileName))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fileName").value(fileName))
                .andExpect(jsonPath("$.analysis").value(analysis))
                .andExpect(jsonPath("$.aiProvider").value("Claude"));
    }

    @Test
    void testAnalyzeWithProvider_V1_Claude_Success() throws Exception {
        // Arrange
        String fileName = "test-resume.pdf";
        String provider = "Claude";
        String analysis = "Claude-specific analysis";
        ResumeAnalysisResponse expectedResponse = new ResumeAnalysisResponse();
        expectedResponse.setFileName(fileName);
        expectedResponse.setAnalysis(analysis);
        expectedResponse.setAiProvider(provider);
        
        when(resumeAnalysisService.analyzeResumeWithProvider(fileName, provider)).thenReturn(expectedResponse);

        // Act & Assert
        mockMvc.perform(get("/api/v1/resume/analyze/{fileName}/provider/{providerName}", fileName, provider))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fileName").value(fileName))
                .andExpect(jsonPath("$.analysis").value(analysis))
                .andExpect(jsonPath("$.aiProvider").value(provider));
    }

    @Test
    void testAnalyzeWithProvider_V1_Gemini_Success() throws Exception {
        // Arrange
        String fileName = "test-resume.pdf";
        String provider = "Gemini";
        String analysis = "Gemini-specific analysis";
        ResumeAnalysisResponse expectedResponse = new ResumeAnalysisResponse();
        expectedResponse.setFileName(fileName);
        expectedResponse.setAnalysis(analysis);
        expectedResponse.setAiProvider(provider);
        
        when(resumeAnalysisService.analyzeResumeWithProvider(fileName, provider)).thenReturn(expectedResponse);

        // Act & Assert
        mockMvc.perform(get("/api/v1/resume/analyze/{fileName}/provider/{providerName}", fileName, provider))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fileName").value(fileName))
                .andExpect(jsonPath("$.analysis").value(analysis))
                .andExpect(jsonPath("$.aiProvider").value(provider));
    }
}