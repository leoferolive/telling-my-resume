package com.tellingmyresume.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.tellingmyresume.dto.response.ProviderStatusResponse;
import com.tellingmyresume.service.impl.AIProviderServiceImpl;

@ExtendWith(MockitoExtension.class)
class AIProviderServiceTest {

    @Mock
    private AIAnalysisService claudeService;
    
    @Mock
    private AIAnalysisService geminiService;

    private AIProviderService aiProviderService;

    @BeforeEach
    void setUp() {
        aiProviderService = new AIProviderServiceImpl(claudeService, geminiService);
    }

    @Test
    void testGetAvailableProviders_BothAvailable() {
        // Arrange
        when(claudeService.isServiceAvailable()).thenReturn(true);
        when(geminiService.isServiceAvailable()).thenReturn(true);

        // Act
        List<String> availableProviders = aiProviderService.getAvailableProviders();

        // Assert
        assertNotNull(availableProviders);
        assertEquals(2, availableProviders.size());
        assertTrue(availableProviders.contains("Claude"));
        assertTrue(availableProviders.contains("Gemini"));
    }

    @Test
    void testGetAvailableProviders_OnlyClaudeAvailable() {
        // Arrange
        when(claudeService.isServiceAvailable()).thenReturn(true);
        when(geminiService.isServiceAvailable()).thenReturn(false);

        // Act
        List<String> availableProviders = aiProviderService.getAvailableProviders();

        // Assert
        assertNotNull(availableProviders);
        assertEquals(1, availableProviders.size());
        assertTrue(availableProviders.contains("Claude"));
        assertFalse(availableProviders.contains("Gemini"));
    }

    @Test
    void testGetAvailableProviders_OnlyGeminiAvailable() {
        // Arrange
        when(claudeService.isServiceAvailable()).thenReturn(false);
        when(geminiService.isServiceAvailable()).thenReturn(true);

        // Act
        List<String> availableProviders = aiProviderService.getAvailableProviders();

        // Assert
        assertNotNull(availableProviders);
        assertEquals(1, availableProviders.size());
        assertTrue(availableProviders.contains("Gemini"));
        assertFalse(availableProviders.contains("Claude"));
    }

    @Test
    void testGetAvailableProviders_NoneAvailable() {
        // Arrange
        when(claudeService.isServiceAvailable()).thenReturn(false);
        when(geminiService.isServiceAvailable()).thenReturn(false);

        // Act
        List<String> availableProviders = aiProviderService.getAvailableProviders();

        // Assert
        assertNotNull(availableProviders);
        assertTrue(availableProviders.isEmpty());
    }

    @Test
    void testGetProviderStatus_BothAvailable() {
        // Arrange
        when(claudeService.isServiceAvailable()).thenReturn(true);
        when(geminiService.isServiceAvailable()).thenReturn(true);

        // Act
        Map<String, Boolean> providerStatus = aiProviderService.getProviderStatus();

        // Assert
        assertNotNull(providerStatus);
        assertEquals(2, providerStatus.size());
        assertTrue(providerStatus.get("Claude"));
        assertTrue(providerStatus.get("Gemini"));
    }

    @Test
    void testGetProviderStatus_MixedAvailability() {
        // Arrange
        when(claudeService.isServiceAvailable()).thenReturn(true);
        when(geminiService.isServiceAvailable()).thenReturn(false);

        // Act
        Map<String, Boolean> providerStatus = aiProviderService.getProviderStatus();

        // Assert
        assertNotNull(providerStatus);
        assertEquals(2, providerStatus.size());
        assertTrue(providerStatus.get("Claude"));
        assertFalse(providerStatus.get("Gemini"));
    }

    @Test
    void testGetSystemStatus_AllProvidersAvailable() {
        // Arrange
        when(claudeService.isServiceAvailable()).thenReturn(true);
        when(geminiService.isServiceAvailable()).thenReturn(true);

        // Act
        ProviderStatusResponse systemStatus = aiProviderService.getSystemStatus();

        // Assert
        assertNotNull(systemStatus);
        assertNotNull(systemStatus.getProviderStatus());
        assertNotNull(systemStatus.getAvailableProviders());
        assertNotNull(systemStatus.getCheckedAt());
        
        assertEquals(2, systemStatus.getProviderStatus().size());
        assertEquals(2, systemStatus.getAvailableProviders().size());
        assertEquals("Claude", systemStatus.getPreferredProvider());
        assertTrue(systemStatus.isHasAvailableProvider());
    }

    @Test
    void testGetSystemStatus_NoProvidersAvailable() {
        // Arrange
        when(claudeService.isServiceAvailable()).thenReturn(false);
        when(geminiService.isServiceAvailable()).thenReturn(false);

        // Act
        ProviderStatusResponse systemStatus = aiProviderService.getSystemStatus();

        // Assert
        assertNotNull(systemStatus);
        assertNotNull(systemStatus.getProviderStatus());
        assertNotNull(systemStatus.getAvailableProviders());
        
        assertEquals(2, systemStatus.getProviderStatus().size());
        assertTrue(systemStatus.getAvailableProviders().isEmpty());
        assertNull(systemStatus.getPreferredProvider());
        assertFalse(systemStatus.isHasAvailableProvider());
    }

    @Test
    void testGetPreferredProvider_ClaudePriority() {
        // Arrange
        when(claudeService.isServiceAvailable()).thenReturn(true);
        // Gemini stub not needed since Claude takes priority

        // Act
        String preferredProvider = aiProviderService.getPreferredProvider();

        // Assert
        assertEquals("Claude", preferredProvider);
    }

    @Test
    void testGetPreferredProvider_FallbackToGemini() {
        // Arrange
        when(claudeService.isServiceAvailable()).thenReturn(false);
        when(geminiService.isServiceAvailable()).thenReturn(true);

        // Act
        String preferredProvider = aiProviderService.getPreferredProvider();

        // Assert
        assertEquals("Gemini", preferredProvider);
    }

    @Test
    void testGetPreferredProvider_NoneAvailable() {
        // Arrange
        when(claudeService.isServiceAvailable()).thenReturn(false);
        when(geminiService.isServiceAvailable()).thenReturn(false);

        // Act
        String preferredProvider = aiProviderService.getPreferredProvider();

        // Assert
        assertNull(preferredProvider);
    }

    @Test
    void testIsProviderAvailable_Claude_True() {
        // Arrange
        when(claudeService.isServiceAvailable()).thenReturn(true);

        // Act
        boolean isAvailable = aiProviderService.isProviderAvailable("Claude");

        // Assert
        assertTrue(isAvailable);
    }

    @Test
    void testIsProviderAvailable_Claude_False() {
        // Arrange
        when(claudeService.isServiceAvailable()).thenReturn(false);

        // Act
        boolean isAvailable = aiProviderService.isProviderAvailable("Claude");

        // Assert
        assertFalse(isAvailable);
    }

    @Test
    void testIsProviderAvailable_Gemini_True() {
        // Arrange
        when(geminiService.isServiceAvailable()).thenReturn(true);

        // Act
        boolean isAvailable = aiProviderService.isProviderAvailable("Gemini");

        // Assert
        assertTrue(isAvailable);
    }

    @Test
    void testIsProviderAvailable_UnknownProvider() {
        // Act
        boolean isAvailable = aiProviderService.isProviderAvailable("UnknownProvider");

        // Assert
        assertFalse(isAvailable);
    }

    @Test
    void testIsProviderAvailable_NullProvider() {
        // Act & Assert
        // The Map.get(null) will return null, and null check will return false
        boolean isAvailable = aiProviderService.isProviderAvailable(null);
        assertFalse(isAvailable);
    }
}