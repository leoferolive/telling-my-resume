package com.tellingmyresume.service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.anthropic.AnthropicChatModel;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.tellingmyresume.exception.ClaudeServiceException;

@Service
public class ClaudeService implements AIAnalysisService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClaudeService.class);

    private final AnthropicChatModel chatModel;

    @Value("classpath:/prompts/systempromt.st")
    private Resource systemPromptResource;

    public ClaudeService(AnthropicChatModel chatModel) {
        this.chatModel = chatModel;
    }

    @Override
    public String generateResume(String resumeContent) throws ClaudeServiceException {
        if (resumeContent == null || resumeContent.trim().isEmpty()) {
            throw new ClaudeServiceException("Conteúdo do currículo não pode estar vazio");
        }
        
        try {
            var system = new SystemMessage(systemPromptResource);
            var user = new UserMessage("Segue currículo: " + resumeContent);

            Prompt prompt = new Prompt(List.of(system, user));

            ChatResponse chatResponse = chatModel.call(prompt);
            LOGGER.debug("Claude response message type: {}", chatResponse.getResult().getOutput().getMessageType());

            String response = chatResponse.getResult().getOutput().getText();
            if (response == null || response.trim().isEmpty()) {
                throw new ClaudeServiceException("Resposta vazia recebida do serviço Claude");
            }

            return response;
        } catch (ClaudeServiceException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.error("Erro inesperado ao chamar a API do Claude: {}", e.getMessage(), e);
            throw new ClaudeServiceException("Erro ao gerar resumo com Claude: " + e.getMessage(), e);
        }
    }

    @Override
    @Async
    public CompletableFuture<String> generateResumeAsync(String resumeContent) {
        try {
            String result = generateResume(resumeContent);
            return CompletableFuture.completedFuture(result);
        } catch (Exception e) {
            CompletableFuture<String> future = new CompletableFuture<>();
            future.completeExceptionally(e);
            return future;
        }
    }

    @Override
    public String getProviderName() {
        return "Claude";
    }

    @Override
    public boolean isServiceAvailable() {
        try {
            return chatModel != null && systemPromptResource.exists();
        } catch (Exception e) {
            LOGGER.warn("Falha ao verificar disponibilidade do serviço Claude: {}", e.getMessage());
            return false;
        }
    }
}
