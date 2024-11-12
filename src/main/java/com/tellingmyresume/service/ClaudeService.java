package com.tellingmyresume.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.anthropic.AnthropicChatModel;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

@Service
public class ClaudeService {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ClaudeService.class);
	
	private final AnthropicChatModel chatModel;
	
	@Value("classpath:/prompts/systempromt.st")
	private Resource systemPromptResource;
	
	public ClaudeService(AnthropicChatModel chatModel) {
		this.chatModel = chatModel;
	}
	
	public String generateResume(String resumeContent) {
		var system = new SystemMessage(systemPromptResource);
		var user = new UserMessage("Segue currículo: " + resumeContent);
		
		Prompt prompt = new Prompt(List.of(system, user));
		
		try {
			ChatResponse chatResponse = chatModel.call(prompt); 
			LOGGER.error(chatResponse.getResult().getOutput().getMessageType().toString());
			
			return chatResponse.getResult().getOutput().getContent();
		} catch (Exception e) {
			throw new RuntimeException("Erro ao gerar resumo com Claude", e);
		}
		
	}
	
	
	
}
