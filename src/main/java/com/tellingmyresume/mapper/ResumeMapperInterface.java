package com.tellingmyresume.mapper;

import org.springframework.web.multipart.MultipartFile;

import com.tellingmyresume.dto.request.ResumeUploadRequest;
import com.tellingmyresume.dto.response.ResumeAnalysisResponse;
import com.tellingmyresume.dto.response.ResumeContentResponse;
import com.tellingmyresume.dto.response.ResumeUploadResponse;

public interface ResumeMapperInterface {

    ResumeUploadResponse toUploadResponse(String fileName, MultipartFile file);

    ResumeUploadResponse toUploadErrorResponse(String fileName, String errorMessage);

    ResumeContentResponse toContentResponse(String fileName, String content, String contentType);

    ResumeAnalysisResponse toAnalysisResponse(String fileName, String analysis, String aiProvider);

    ResumeAnalysisResponse toAnalysisErrorResponse(String fileName, String errorMessage, String aiProvider);

    String extractFileName(ResumeUploadRequest request);
}