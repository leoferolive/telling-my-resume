package com.tellingmyresume.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import com.tellingmyresume.dto.response.ProviderStatusResponse;
import com.tellingmyresume.service.AIProviderService;

import static org.mockito.Mockito.when;

@WebMvcTest(AIProviderController.class)
class AIProviderControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AIProviderService aiProviderService;

    @Test
    void testGetSystemStatus_Success() throws Exception {
        // Arrange
        Map<String, Boolean> providerStatus = Map.of("Claude", true, "Gemini", false);
        List<String> availableProviders = Arrays.asList("Claude");
        ProviderStatusResponse expectedResponse = new ProviderStatusResponse(
            providerStatus, availableProviders, "Claude", true);
        
        when(aiProviderService.getSystemStatus()).thenReturn(expectedResponse);

        // Act & Assert
        mockMvc.perform(get("/ai/status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.providerStatus.Claude").value(true))
                .andExpect(jsonPath("$.providerStatus.Gemini").value(false))
                .andExpect(jsonPath("$.availableProviders[0]").value("Claude"))
                .andExpect(jsonPath("$.preferredProvider").value("Claude"))
                .andExpect(jsonPath("$.hasAvailableProvider").value(true));
    }

    @Test
    void testGetAvailableProviders_Success() throws Exception {
        // Arrange
        List<String> availableProviders = Arrays.asList("Claude", "Gemini");
        
        when(aiProviderService.getAvailableProviders()).thenReturn(availableProviders);

        // Act & Assert
        mockMvc.perform(get("/ai/providers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").value("Claude"))
                .andExpect(jsonPath("$[1]").value("Gemini"));
    }

    @Test
    void testGetPreferredProvider_Success() throws Exception {
        // Arrange
        String preferredProvider = "Claude";
        
        when(aiProviderService.getPreferredProvider()).thenReturn(preferredProvider);

        // Act & Assert
        mockMvc.perform(get("/ai/preferred"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.preferred").value("Claude"));
    }

    @Test
    void testGetProviderStatus_Claude_Available() throws Exception {
        // Arrange
        when(aiProviderService.isProviderAvailable("Claude")).thenReturn(true);

        // Act & Assert
        mockMvc.perform(get("/ai/providers/{provider}/status", "Claude"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.Claude").value(true));
    }

    @Test
    void testGetProviderStatus_Gemini_Unavailable() throws Exception {
        // Arrange
        when(aiProviderService.isProviderAvailable("Gemini")).thenReturn(false);

        // Act & Assert
        mockMvc.perform(get("/ai/providers/{provider}/status", "Gemini"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.Gemini").value(false));
    }

    @Test
    void testGetSystemStatus_NoProvidersAvailable() throws Exception {
        // Arrange
        Map<String, Boolean> providerStatus = Map.of("Claude", false, "Gemini", false);
        List<String> availableProviders = Arrays.asList();
        ProviderStatusResponse expectedResponse = new ProviderStatusResponse(
            providerStatus, availableProviders, null, false);
        
        when(aiProviderService.getSystemStatus()).thenReturn(expectedResponse);

        // Act & Assert
        mockMvc.perform(get("/ai/status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.providerStatus.Claude").value(false))
                .andExpect(jsonPath("$.providerStatus.Gemini").value(false))
                .andExpect(jsonPath("$.availableProviders").isEmpty())
                .andExpect(jsonPath("$.preferredProvider").doesNotExist())
                .andExpect(jsonPath("$.hasAvailableProvider").value(false));
    }
}