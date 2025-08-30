package com.tellingmyresume.service;

import java.util.concurrent.CompletableFuture;

import com.tellingmyresume.dto.request.ResumeUploadRequest;
import com.tellingmyresume.dto.response.ResumeAnalysisResponse;
import com.tellingmyresume.dto.response.ResumeContentResponse;
import com.tellingmyresume.dto.response.ResumeUploadResponse;
import com.tellingmyresume.exception.AIServiceException;
import com.tellingmyresume.exception.ResumeNotFoundException;
import com.tellingmyresume.exception.ResumeStorageException;

public interface ResumeAnalysisService {
    
    ResumeUploadResponse uploadResume(ResumeUploadRequest request) throws ResumeStorageException;
    
    ResumeContentResponse getResumeContent(String fileName) throws ResumeNotFoundException;
    
    ResumeAnalysisResponse analyzeResumeWithProvider(String fileName, String aiProvider) 
            throws ResumeNotFoundException, AIServiceException;
    
    CompletableFuture<ResumeAnalysisResponse> analyzeResumeAsync(String fileName, String aiProvider) 
            throws ResumeNotFoundException;
    
    ResumeAnalysisResponse analyzeResumeWithBestAvailable(String fileName) 
            throws ResumeNotFoundException, AIServiceException;
            
    boolean isResumeAvailable(String fileName);
    
    void deleteResume(String fileName) throws ResumeNotFoundException;
}