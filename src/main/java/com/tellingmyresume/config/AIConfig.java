package com.tellingmyresume.config;

import io.micrometer.observation.ObservationRegistry;
import org.springframework.ai.anthropic.AnthropicChatModel;
import org.springframework.ai.anthropic.AnthropicChatOptions;
import org.springframework.ai.anthropic.api.AnthropicApi;
import org.springframework.ai.model.tool.ToolCallingManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.support.RetryTemplate;

@Configuration
public class AIConfig {

    @Value("${spring.ai.anthropic.api-key}")
    private String anthropicApiKey;

    @Bean
    public AnthropicChatModel anthropicChatModel() {
        AnthropicApi anthropicApi = AnthropicApi.builder()
            .baseUrl(AnthropicApi.DEFAULT_BASE_URL)
            .apiKey(anthropicApiKey)
            .build();
            
        AnthropicChatOptions defaultOptions = AnthropicChatOptions.builder().build();
        ToolCallingManager toolCallingManager = ToolCallingManager.builder().build();
        RetryTemplate retryTemplate = RetryTemplate.builder().maxAttempts(3).build();
        ObservationRegistry observationRegistry = ObservationRegistry.create();
            
        return new AnthropicChatModel(anthropicApi, defaultOptions, toolCallingManager, retryTemplate, observationRegistry);
    }
}