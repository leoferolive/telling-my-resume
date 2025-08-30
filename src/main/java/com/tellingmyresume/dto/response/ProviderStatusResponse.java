package com.tellingmyresume.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class ProviderStatusResponse {
    
    private Map<String, Boolean> providerStatus;
    private List<String> availableProviders;
    private String preferredProvider;
    private boolean hasAvailableProvider;
    private LocalDateTime checkedAt;
    
    public ProviderStatusResponse() {
        this.checkedAt = LocalDateTime.now();
    }
    
    public ProviderStatusResponse(Map<String, Boolean> providerStatus, 
                                List<String> availableProviders, 
                                String preferredProvider, 
                                boolean hasAvailableProvider) {
        this();
        this.providerStatus = providerStatus;
        this.availableProviders = availableProviders;
        this.preferredProvider = preferredProvider;
        this.hasAvailableProvider = hasAvailableProvider;
    }

    public Map<String, Boolean> getProviderStatus() {
        return providerStatus;
    }

    public void setProviderStatus(Map<String, Boolean> providerStatus) {
        this.providerStatus = providerStatus;
    }

    public List<String> getAvailableProviders() {
        return availableProviders;
    }

    public void setAvailableProviders(List<String> availableProviders) {
        this.availableProviders = availableProviders;
    }

    public String getPreferredProvider() {
        return preferredProvider;
    }

    public void setPreferredProvider(String preferredProvider) {
        this.preferredProvider = preferredProvider;
    }

    public boolean isHasAvailableProvider() {
        return hasAvailableProvider;
    }

    public void setHasAvailableProvider(boolean hasAvailableProvider) {
        this.hasAvailableProvider = hasAvailableProvider;
    }

    public LocalDateTime getCheckedAt() {
        return checkedAt;
    }

    public void setCheckedAt(LocalDateTime checkedAt) {
        this.checkedAt = checkedAt;
    }
}