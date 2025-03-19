package com.tellingmyresume.service;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.tellingmyresume.model.Resume;
import com.tellingmyresume.repository.ResumeRepository;

@Service
public class DatabaseStorageService {
    
    @Autowired
    private ResumeRepository resumeRepository;
    
    public void save(String fileName, MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IOException("Arquivo está vazio");
        }
        
        if (file.getSize() > 10 * 1024 * 1024) { // 10MB
            throw new IOException("Arquivo excede o tamanho máximo permitido de 10MB");
        }
        
        Resume resume = new Resume(
            fileName,
            file.getContentType(),
            file.getBytes()
        );
        
        resumeRepository.save(resume);
    }
    
    public byte[] read(String fileName) throws IOException {
        Resume resume = resumeRepository.findByFileName(fileName);
        if (resume == null) {
            throw new IOException("Arquivo não encontrado: " + fileName);
        }
        return resume.getContent();
    }
    
    public boolean fileExists(String fileName) {
        return resumeRepository.existsByFileName(fileName);
    }
} 