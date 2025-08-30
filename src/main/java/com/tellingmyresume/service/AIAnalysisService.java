package com.tellingmyresume.service;

import java.util.concurrent.CompletableFuture;

import com.tellingmyresume.exception.AIServiceException;

public interface AIAnalysisService {
    
    String generateResume(String resumeContent) throws AIServiceException;
    
    CompletableFuture<String> generateResumeAsync(String resumeContent);
    
    String getProviderName();
    
    boolean isServiceAvailable();
}