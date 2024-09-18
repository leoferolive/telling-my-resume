package com.tellingmyresume.service;

import java.io.IOException;

import org.springframework.stereotype.Service;

import com.tellingmyresume.exception.ResumeNotFoundException;
import com.tellingmyresume.exception.ResumeStorageException;
import com.tellingmyresume.storage.LocalStorage;

@Service
public class ResumeService {

    private final LocalStorage localStorage;
    
    public ResumeService(LocalStorage localStorage) {
        this.localStorage = localStorage;
    }

    public void saveResume(String fileName, String content) throws IOException {
        try {
            localStorage.save(fileName, content);
        } catch (IOException e) {
            throw new ResumeStorageException("Erro ao salvar o currículo: " + fileName, e);
        }
    }

    public String readResume(String fileName) throws IOException {
        try {
            return localStorage.read(fileName);
        } catch (IOException e) {
            throw new ResumeNotFoundException("Currículo não encontrado: " + fileName, e);
        }
    }
}
