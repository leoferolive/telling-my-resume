package com.tellingmyresume.service.impl;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.tellingmyresume.dto.request.ResumeUploadRequest;
import com.tellingmyresume.dto.response.ResumeAnalysisResponse;
import com.tellingmyresume.dto.response.ResumeContentResponse;
import com.tellingmyresume.dto.response.ResumeUploadResponse;
import com.tellingmyresume.exception.AIServiceException;
import com.tellingmyresume.exception.GenericAIServiceException;
import com.tellingmyresume.exception.ResumeNotFoundException;
import com.tellingmyresume.exception.ResumeStorageException;
import com.tellingmyresume.mapper.ResumeMapperInterface;
import com.tellingmyresume.service.AIAnalysisService;
import com.tellingmyresume.service.ResumeAnalysisService;
import com.tellingmyresume.service.ResumeDataService;

@Service
public class ResumeAnalysisServiceImpl implements ResumeAnalysisService {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(ResumeAnalysisServiceImpl.class);
    
    private final ResumeDataService resumeService;
    private final AIAnalysisService claudeService;
    private final AIAnalysisService geminiService;
    private final ResumeMapperInterface resumeMapper;
    
    public ResumeAnalysisServiceImpl(ResumeDataService resumeService,
                                   @Qualifier("claudeService") AIAnalysisService claudeService,
                                   @Qualifier("geminiService") AIAnalysisService geminiService,
                                   ResumeMapperInterface resumeMapper) {
        this.resumeService = resumeService;
        this.claudeService = claudeService;
        this.geminiService = geminiService;
        this.resumeMapper = resumeMapper;
    }

    @Override
    public ResumeUploadResponse uploadResume(ResumeUploadRequest request) throws ResumeStorageException {
        String fileName = resumeMapper.extractFileName(request);
        
        try {
            resumeService.saveResume(fileName, request.getFile());
            return resumeMapper.toUploadResponse(fileName, request.getFile());
        } catch (ResumeStorageException e) {
            LOGGER.error("Falha ao fazer upload do arquivo {}: {}", fileName, e.getMessage());
            return resumeMapper.toUploadErrorResponse(fileName, e.getMessage());
        }
    }

    @Override
    public ResumeContentResponse getResumeContent(String fileName) throws ResumeNotFoundException {
        String content = resumeService.readResume(fileName);
        return resumeMapper.toContentResponse(fileName, content, "text/plain");
    }

    @Override
    public ResumeAnalysisResponse analyzeResumeWithProvider(String fileName, String aiProvider) 
            throws ResumeNotFoundException, AIServiceException {
        
        String resumeContent = resumeService.readResume(fileName);
        AIAnalysisService analysisService = getAnalysisService(aiProvider);
        
        if (!analysisService.isServiceAvailable()) {
            throw new GenericAIServiceException(aiProvider, "Serviço " + aiProvider + " não está disponível");
        }
        
        try {
            String analysis = analysisService.generateResume(resumeContent);
            return resumeMapper.toAnalysisResponse(fileName, analysis, aiProvider);
        } catch (AIServiceException e) {
            LOGGER.error("Erro ao analisar currículo {} com {}: {}", fileName, aiProvider, e.getMessage());
            return resumeMapper.toAnalysisErrorResponse(fileName, e.getMessage(), aiProvider);
        }
    }

    @Override
    @Async
    public CompletableFuture<ResumeAnalysisResponse> analyzeResumeAsync(String fileName, String aiProvider) 
            throws ResumeNotFoundException {
        try {
            ResumeAnalysisResponse result = analyzeResumeWithProvider(fileName, aiProvider);
            return CompletableFuture.completedFuture(result);
        } catch (AIServiceException e) {
            ResumeAnalysisResponse errorResponse = resumeMapper.toAnalysisErrorResponse(
                fileName, e.getMessage(), aiProvider);
            return CompletableFuture.completedFuture(errorResponse);
        }
    }

    @Override
    public ResumeAnalysisResponse analyzeResumeWithBestAvailable(String fileName) 
            throws ResumeNotFoundException, AIServiceException {
        
        List<AIAnalysisService> services = List.of(claudeService, geminiService);
        
        for (AIAnalysisService service : services) {
            if (service.isServiceAvailable()) {
                try {
                    return analyzeResumeWithProvider(fileName, service.getProviderName());
                } catch (AIServiceException e) {
                    LOGGER.warn("Falha ao usar serviço {}: {}", service.getProviderName(), e.getMessage());
                    continue;
                }
            }
        }
        
        throw new GenericAIServiceException("System", "Nenhum serviço de IA está disponível");
    }

    @Override
    public boolean isResumeAvailable(String fileName) {
        return resumeService.resumeExists(fileName);
    }

    @Override
    public void deleteResume(String fileName) throws ResumeNotFoundException {
        resumeService.deleteResume(fileName);
    }
    
    private AIAnalysisService getAnalysisService(String aiProvider) throws AIServiceException {
        return switch (aiProvider.toLowerCase()) {
            case "claude" -> claudeService;
            case "gemini" -> geminiService;
            default -> throw new GenericAIServiceException(aiProvider, "Provedor de IA não suportado: " + aiProvider);
        };
    }
}