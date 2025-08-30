package com.tellingmyresume.service;

import org.springframework.web.multipart.MultipartFile;

import com.tellingmyresume.exception.ResumeNotFoundException;
import com.tellingmyresume.exception.ResumeStorageException;

public interface ResumeDataService {
    
    void saveResume(String fileName, MultipartFile file) throws ResumeStorageException;
    
    String readResume(String fileName) throws ResumeNotFoundException;
    
    boolean resumeExists(String fileName);
    
    void deleteResume(String fileName) throws ResumeNotFoundException;
}