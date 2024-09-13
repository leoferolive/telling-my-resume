package com.tellingmyresume.service;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.tellingmyresume.storage.LocalStorage;

@Service
public class ResumeService {

    private final LocalStorage localStorage;
    private static final Logger logger = LoggerFactory.getLogger(ResumeService.class);
    
    public ResumeService(LocalStorage localStorage) {
        this.localStorage = localStorage;
    }

    public void saveResume(String fileName, String content) throws IOException {
    	logger.info("Salvando currículo: " + fileName);
    	localStorage.save(fileName, content);
    }

    public String readResume(String fileName) throws IOException {
        try {
        	logger.info("Lendo currículo: " + fileName);
        	return localStorage.read(fileName);
        } catch (IOException e) {
            throw new FileNotFoundException("Currículo " + fileName + " não encontrado.");
        }
    }
}
