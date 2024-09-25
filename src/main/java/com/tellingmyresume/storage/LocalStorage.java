package com.tellingmyresume.storage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class LocalStorage {

    public static final String STORAGE_PATH = "curriculos/";

    /**
     * Salva o arquivo enviado no diretório especificado.
     * 
     * @param fileName Nome do arquivo a ser salvo.
     * @param file     Arquivo Multipart enviado.
     * @throws IOException Se ocorrer algum erro ao salvar o arquivo.
     */
    public void save(String fileName, MultipartFile file) throws IOException {
        // Garante que o diretório existe
        Files.createDirectories(Paths.get(STORAGE_PATH));

        // Salva o arquivo com o nome original no diretório especificado
        Path destinationPath = Paths.get(STORAGE_PATH + fileName);
        Files.write(destinationPath, file.getBytes());
    }

    public byte[] read(String fileName) throws IOException {
        Path filePath = Paths.get(STORAGE_PATH + fileName);
        return Files.readAllBytes(filePath); // Retorna o conteúdo bruto do arquivo como bytes
    }
}