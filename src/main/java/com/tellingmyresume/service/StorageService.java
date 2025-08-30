package com.tellingmyresume.service;

import org.springframework.web.multipart.MultipartFile;

import com.tellingmyresume.exception.StorageException;

public interface StorageService {
    
    void save(String fileName, MultipartFile file) throws StorageException;
    
    byte[] read(String fileName) throws StorageException;
    
    boolean fileExists(String fileName);
    
    void delete(String fileName) throws StorageException;
}