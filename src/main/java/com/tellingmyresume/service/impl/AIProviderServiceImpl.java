package com.tellingmyresume.service.impl;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.tellingmyresume.dto.response.ProviderStatusResponse;
import com.tellingmyresume.service.AIAnalysisService;
import com.tellingmyresume.service.AIProviderService;

@Service
public class AIProviderServiceImpl implements AIProviderService {
    
    private final AIAnalysisService claudeService;
    private final AIAnalysisService geminiService;
    private final Map<String, AIAnalysisService> providerMap;
    
    public AIProviderServiceImpl(@Qualifier("claudeService") AIAnalysisService claudeService,
                               @Qualifier("geminiService") AIAnalysisService geminiService) {
        this.claudeService = claudeService;
        this.geminiService = geminiService;
        this.providerMap = Map.of(
            "Claude", claudeService,
            "Gemini", geminiService
        );
    }

    @Override
    public List<String> getAvailableProviders() {
        return providerMap.entrySet().stream()
                .filter(entry -> entry.getValue().isServiceAvailable())
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    @Override
    public Map<String, Boolean> getProviderStatus() {
        return providerMap.entrySet().stream()
                .collect(Collectors.toMap(
                    Map.Entry::getKey,
                    entry -> entry.getValue().isServiceAvailable()
                ));
    }

    @Override
    public ProviderStatusResponse getSystemStatus() {
        Map<String, Boolean> status = getProviderStatus();
        List<String> available = getAvailableProviders();
        String preferred = getPreferredProvider();
        boolean hasAvailable = !available.isEmpty();
        
        return new ProviderStatusResponse(status, available, preferred, hasAvailable);
    }

    @Override
    public String getPreferredProvider() {
        // Priority: Claude first, then Gemini
        if (claudeService.isServiceAvailable()) {
            return "Claude";
        }
        if (geminiService.isServiceAvailable()) {
            return "Gemini";
        }
        return null;
    }

    @Override
    public boolean isProviderAvailable(String providerName) {
        AIAnalysisService service = providerMap.get(providerName);
        return service != null && service.isServiceAvailable();
    }
}