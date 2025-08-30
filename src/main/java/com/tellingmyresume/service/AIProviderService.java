package com.tellingmyresume.service;

import java.util.List;
import java.util.Map;

import com.tellingmyresume.dto.response.ProviderStatusResponse;

public interface AIProviderService {
    
    List<String> getAvailableProviders();
    
    Map<String, Boolean> getProviderStatus();
    
    ProviderStatusResponse getSystemStatus();
    
    String getPreferredProvider();
    
    boolean isProviderAvailable(String providerName);
}