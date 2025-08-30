package com.tellingmyresume.mapper;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.tellingmyresume.dto.request.ResumeUploadRequest;
import com.tellingmyresume.dto.response.ResumeAnalysisResponse;
import com.tellingmyresume.dto.response.ResumeContentResponse;
import com.tellingmyresume.dto.response.ResumeUploadResponse;
import com.tellingmyresume.model.Resume;
import com.tellingmyresume.util.FileNameSanitizer;

@Component
public class ResumeMapper {

    public ResumeUploadResponse toUploadResponse(String fileName, MultipartFile file) {
        String sanitizedFileName = FileNameSanitizer.sanitize(fileName);
        return new ResumeUploadResponse(
            sanitizedFileName,
            "Arquivo enviado com sucesso!",
            file.getSize()
        );
    }

    public ResumeUploadResponse toUploadErrorResponse(String fileName, String errorMessage) {
        return new ResumeUploadResponse(fileName, errorMessage);
    }

    public ResumeContentResponse toContentResponse(String fileName, String content, String contentType) {
        return new ResumeContentResponse(fileName, content, contentType);
    }

    public ResumeAnalysisResponse toAnalysisResponse(String fileName, String analysis, String aiProvider) {
        return ResumeAnalysisResponse.success(fileName, analysis, aiProvider);
    }

    public ResumeAnalysisResponse toAnalysisErrorResponse(String fileName, String errorMessage, String aiProvider) {
        return ResumeAnalysisResponse.error(fileName, errorMessage, aiProvider);
    }

    public String extractFileName(ResumeUploadRequest request) {
        MultipartFile file = request.getFile();
        String fileName = request.getCustomFileName();
        
        if (fileName == null || fileName.trim().isEmpty()) {
            fileName = file.getOriginalFilename();
        }
        
        return FileNameSanitizer.sanitize(fileName);
    }
}