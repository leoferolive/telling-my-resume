package com.tellingmyresume.dto.response;

import java.time.LocalDateTime;

public class ResumeUploadResponse {

    private String fileName;
    private String message;
    private LocalDateTime uploadedAt;
    private Long fileSize;

    public ResumeUploadResponse() {
    }

    public ResumeUploadResponse(String fileName, String message) {
        this.fileName = fileName;
        this.message = message;
        this.uploadedAt = LocalDateTime.now();
    }

    public ResumeUploadResponse(String fileName, String message, Long fileSize) {
        this.fileName = fileName;
        this.message = message;
        this.fileSize = fileSize;
        this.uploadedAt = LocalDateTime.now();
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getUploadedAt() {
        return uploadedAt;
    }

    public void setUploadedAt(LocalDateTime uploadedAt) {
        this.uploadedAt = uploadedAt;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }
}