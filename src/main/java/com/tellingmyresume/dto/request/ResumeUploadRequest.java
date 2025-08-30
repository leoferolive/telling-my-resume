package com.tellingmyresume.dto.request;

import org.springframework.web.multipart.MultipartFile;

import com.tellingmyresume.validation.FileSize;
import com.tellingmyresume.validation.FileType;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class ResumeUploadRequest {

    @NotNull(message = "Arquivo é obrigatório")
    @FileType(value = {"pdf", "docx", "txt"}, message = "Apenas arquivos PDF, DOCX e TXT são aceitos")
    @FileSize(max = 10, message = "Arquivo deve ter no máximo 10MB")
    private MultipartFile file;

    @Size(max = 255, message = "Nome do arquivo deve ter no máximo 255 caracteres")
    private String customFileName;

    public ResumeUploadRequest() {
    }

    public ResumeUploadRequest(MultipartFile file) {
        this.file = file;
    }

    public MultipartFile getFile() {
        return file;
    }

    public void setFile(MultipartFile file) {
        this.file = file;
    }

    public String getCustomFileName() {
        return customFileName;
    }

    public void setCustomFileName(String customFileName) {
        this.customFileName = customFileName;
    }
}