package com.tellingmyresume.dto.response;

import java.time.LocalDateTime;

public class ResumeAnalysisResponse {

    private String fileName;
    private String analysis;
    private String aiProvider;
    private LocalDateTime analyzedAt;
    private boolean success;

    public ResumeAnalysisResponse() {
    }

    public ResumeAnalysisResponse(String fileName, String analysis, String aiProvider) {
        this.fileName = fileName;
        this.analysis = analysis;
        this.aiProvider = aiProvider;
        this.analyzedAt = LocalDateTime.now();
        this.success = true;
    }

    public static ResumeAnalysisResponse success(String fileName, String analysis, String aiProvider) {
        return new ResumeAnalysisResponse(fileName, analysis, aiProvider);
    }

    public static ResumeAnalysisResponse error(String fileName, String message, String aiProvider) {
        ResumeAnalysisResponse response = new ResumeAnalysisResponse();
        response.fileName = fileName;
        response.analysis = message;
        response.aiProvider = aiProvider;
        response.analyzedAt = LocalDateTime.now();
        response.success = false;
        return response;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getAnalysis() {
        return analysis;
    }

    public void setAnalysis(String analysis) {
        this.analysis = analysis;
    }

    public String getAiProvider() {
        return aiProvider;
    }

    public void setAiProvider(String aiProvider) {
        this.aiProvider = aiProvider;
    }

    public LocalDateTime getAnalyzedAt() {
        return analyzedAt;
    }

    public void setAnalyzedAt(LocalDateTime analyzedAt) {
        this.analyzedAt = analyzedAt;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}