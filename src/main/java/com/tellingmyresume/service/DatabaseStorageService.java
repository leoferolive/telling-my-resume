package com.tellingmyresume.service;

import java.io.IOException;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.tellingmyresume.exception.StorageException;
import com.tellingmyresume.model.Resume;
import com.tellingmyresume.repository.ResumeRepository;

@Service
public class DatabaseStorageService implements StorageService {
    
    private final ResumeRepository resumeRepository;
    
    public DatabaseStorageService(ResumeRepository resumeRepository) {
        this.resumeRepository = resumeRepository;
    }
    
    @Override
    public void save(String fileName, MultipartFile file) throws StorageException {
        try {
            if (file.isEmpty()) {
                throw new StorageException("Arquivo está vazio");
            }
            
            if (file.getSize() > 10 * 1024 * 1024) { // 10MB
                throw new StorageException("Arquivo excede o tamanho máximo permitido de 10MB");
            }
            
            Resume resume = new Resume(
                fileName,
                file.getContentType(),
                file.getBytes()
            );
            
            resumeRepository.save(resume);
        } catch (IOException e) {
            throw new StorageException("Falha ao acessar o conteúdo do arquivo", e);
        }
    }
    
    @Override
    public byte[] read(String fileName) throws StorageException {
        Resume resume = resumeRepository.findByFileName(fileName);
        if (resume == null) {
            throw new StorageException("Arquivo não encontrado: " + fileName);
        }
        return resume.getContent();
    }
    
    @Override
    public boolean fileExists(String fileName) {
        return resumeRepository.existsByFileName(fileName);
    }
    
    @Override
    public void delete(String fileName) throws StorageException {
        Resume resume = resumeRepository.findByFileName(fileName);
        if (resume == null) {
            throw new StorageException("Arquivo não encontrado para exclusão: " + fileName);
        }
        resumeRepository.delete(resume);
    }
} 