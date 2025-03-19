package com.tellingmyresume.service;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.tellingmyresume.exception.ResumeNotFoundException;
import com.tellingmyresume.exception.ResumeStorageException;
import com.tellingmyresume.util.FileUtils;

@Service
public class ResumeService {
    
    @Autowired
    private DatabaseStorageService storageService;
    
    @Autowired
    private FileUtils fileUtils;
    
    public void saveResume(String fileName, MultipartFile file) throws ResumeStorageException {
        try {
            storageService.save(fileName, file);
        } catch (IOException e) {
            throw new ResumeStorageException("Falha ao salvar o arquivo: " + e.getMessage());
        }
    }
    
    public String readResume(String fileName) throws ResumeNotFoundException {
        try {
            byte[] content = storageService.read(fileName);
            return fileUtils.extractText(fileName, content);
        } catch (IOException e) {
            throw new ResumeNotFoundException("Falha ao ler o arquivo: " + e.getMessage());
        }
    }
    
    public boolean resumeExists(String fileName) {
        return storageService.fileExists(fileName);
    }
}
