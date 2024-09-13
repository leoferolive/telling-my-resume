package com.tellingmyresume.storage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.springframework.stereotype.Component;

@Component
public class LocalStorage {

    private static final String STORAGE_PATH = "curriculos/";

    public void save(String fileName, String content) throws IOException {
        Files.createDirectories(Paths.get(STORAGE_PATH)); // Garante que o diretório existe
        Files.write(Paths.get(STORAGE_PATH + fileName + ".txt"), content.getBytes());
    }

    public String read(String fileName) throws IOException {
        return new String(Files.readAllBytes(Paths.get(STORAGE_PATH + fileName + ".txt")));
    }
}
