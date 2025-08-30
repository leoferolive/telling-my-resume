package com.tellingmyresume.dto.response;

import java.time.LocalDateTime;

public class ResumeContentResponse {

    private String fileName;
    private String content;
    private String contentType;
    private LocalDateTime retrievedAt;
    private Long contentLength;

    public ResumeContentResponse() {
        this.retrievedAt = LocalDateTime.now();
    }

    public ResumeContentResponse(String fileName, String content, String contentType) {
        this();
        this.fileName = fileName;
        this.content = content;
        this.contentType = contentType;
        this.contentLength = content != null ? (long) content.length() : 0;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
        this.contentLength = content != null ? (long) content.length() : 0;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public LocalDateTime getRetrievedAt() {
        return retrievedAt;
    }

    public void setRetrievedAt(LocalDateTime retrievedAt) {
        this.retrievedAt = retrievedAt;
    }

    public Long getContentLength() {
        return contentLength;
    }

    public void setContentLength(Long contentLength) {
        this.contentLength = contentLength;
    }
}