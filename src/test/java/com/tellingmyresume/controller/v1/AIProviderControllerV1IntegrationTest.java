package com.tellingmyresume.controller.v1;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import com.tellingmyresume.dto.response.ProviderStatusResponse;
import com.tellingmyresume.service.AIProviderService;

import static org.mockito.Mockito.when;

@WebMvcTest(AIProviderControllerV1.class)
class AIProviderControllerV1IntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AIProviderService aiProviderService;

    @MockBean
    private com.tellingmyresume.interceptor.RateLimitingInterceptor rateLimitingInterceptor;

    @MockBean
    private com.tellingmyresume.config.CorrelationIdInterceptor correlationIdInterceptor;

    @Test
    void testGetAvailableProviders_V1_Success() throws Exception {
        // Arrange
        List<String> availableProviders = Arrays.asList("Claude", "Gemini");
        when(aiProviderService.getAvailableProviders()).thenReturn(availableProviders);

        // Act & Assert
        mockMvc.perform(get("/api/v1/ai/providers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").value("Claude"))
                .andExpect(jsonPath("$[1]").value("Gemini"));
    }

    @Test
    void testGetSystemStatus_V1_Success() throws Exception {
        // Arrange
        ProviderStatusResponse status = new ProviderStatusResponse();
        status.setPreferredProvider("Claude");
        status.setHasAvailableProvider(true);
        
        List<String> availableProviders = Arrays.asList("Claude");
        status.setAvailableProviders(availableProviders);
        
        java.util.Map<String, Boolean> providerStatus = new java.util.HashMap<>();
        providerStatus.put("Claude", true);
        providerStatus.put("Gemini", false);
        status.setProviderStatus(providerStatus);
        
        when(aiProviderService.getSystemStatus()).thenReturn(status);

        // Act & Assert
        mockMvc.perform(get("/api/v1/ai/status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.preferredProvider").value("Claude"))
                .andExpect(jsonPath("$.hasAvailableProvider").value(true))
                .andExpect(jsonPath("$.providerStatus.Claude").value(true))
                .andExpect(jsonPath("$.providerStatus.Gemini").value(false));
    }

    @Test
    void testGetProviderStatus_V1_Claude_Success() throws Exception {
        // Arrange
        String providerName = "Claude";
        when(aiProviderService.isProviderAvailable(providerName)).thenReturn(true);

        // Act & Assert
        mockMvc.perform(get("/api/v1/ai/providers/{providerName}/status", providerName))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.Claude").value(true));
    }

    @Test
    void testGetProviderStatus_V1_Gemini_Unavailable() throws Exception {
        // Arrange
        String providerName = "Gemini";
        when(aiProviderService.isProviderAvailable(providerName)).thenReturn(false);

        // Act & Assert
        mockMvc.perform(get("/api/v1/ai/providers/{providerName}/status", providerName))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.Gemini").value(false));
    }

    @Test
    void testGetPreferredProvider_V1_Success() throws Exception {
        // Arrange
        when(aiProviderService.getPreferredProvider()).thenReturn("Claude");

        // Act & Assert
        mockMvc.perform(get("/api/v1/ai/preferred"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.preferred").value("Claude"));
    }

    @Test
    void testGetPreferredProvider_V1_NoProviderAvailable() throws Exception {
        // Arrange
        when(aiProviderService.getPreferredProvider()).thenReturn(null);

        // Act & Assert
        mockMvc.perform(get("/api/v1/ai/preferred"))
                .andExpect(status().is(503))
                .andExpect(jsonPath("$.error").value("Nenhum provedor disponível"));
    }
}