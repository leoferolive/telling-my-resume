package com.tellingmyresume.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.tellingmyresume.exception.ResumeNotFoundException;
import com.tellingmyresume.exception.ResumeStorageException;
import com.tellingmyresume.exception.StorageException;
import com.tellingmyresume.util.FileUtils;

@Service
public class ResumeService {
    
    private final StorageService storageService;
    private final FileUtils fileUtils;
    
    public ResumeService(StorageService storageService, FileUtils fileUtils) {
        this.storageService = storageService;
        this.fileUtils = fileUtils;
    }
    
    public void saveResume(String fileName, MultipartFile file) throws ResumeStorageException {
        try {
            validateResumeFile(file);
            storageService.save(fileName, file);
        } catch (StorageException e) {
            throw new ResumeStorageException("Falha ao salvar o arquivo: " + e.getMessage(), e);
        }
    }
    
    public String readResume(String fileName) throws ResumeNotFoundException {
        try {
            validateFileExists(fileName);
            byte[] content = storageService.read(fileName);
            return fileUtils.extractText(fileName, content);
        } catch (StorageException e) {
            throw new ResumeNotFoundException("Falha ao ler o arquivo: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new ResumeNotFoundException("Erro ao processar o conteúdo do arquivo: " + e.getMessage(), e);
        }
    }
    
    public boolean resumeExists(String fileName) {
        return storageService.fileExists(fileName);
    }
    
    public void deleteResume(String fileName) throws ResumeNotFoundException {
        try {
            validateFileExists(fileName);
            storageService.delete(fileName);
        } catch (StorageException e) {
            throw new ResumeNotFoundException("Falha ao excluir o arquivo: " + e.getMessage(), e);
        }
    }
    
    private void validateResumeFile(MultipartFile file) throws ResumeStorageException {
        if (file == null || file.isEmpty()) {
            throw new ResumeStorageException("Arquivo não pode estar vazio");
        }
        
        String fileName = file.getOriginalFilename();
        if (fileName == null) {
            throw new ResumeStorageException("Nome do arquivo é obrigatório");
        }
        
        if (!isValidFileType(fileName)) {
            throw new ResumeStorageException("Tipo de arquivo não suportado. Apenas PDF, DOCX e TXT são aceitos");
        }
    }
    
    private void validateFileExists(String fileName) throws ResumeNotFoundException {
        if (!storageService.fileExists(fileName)) {
            throw new ResumeNotFoundException("Arquivo não encontrado: " + fileName);
        }
    }
    
    private boolean isValidFileType(String fileName) {
        String extension = fileName.toLowerCase();
        return extension.endsWith(".pdf") || 
               extension.endsWith(".docx") || 
               extension.endsWith(".txt");
    }
}
