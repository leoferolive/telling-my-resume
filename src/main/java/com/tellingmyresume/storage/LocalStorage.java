package com.tellingmyresume.storage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Objects;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class LocalStorage {

    public static final String STORAGE_PATH = "resumes/";
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // Limite de 10 MB

    /**
     * Salva o arquivo enviado no diretório especificado.
     * 
     * @param fileName Nome do arquivo a ser salvo.
     * @param file     Arquivo Multipart enviado.
     * @throws IOException Se ocorrer algum erro ao salvar o arquivo.
     */
    public void save(String fileName, MultipartFile file) throws IOException {
        validateFile(file);

        Path storageDir = getStorageDirectory();
        Path destinationPath = storageDir.resolve(sanitizeFileName(fileName));

        // Salva o arquivo no diretório especificado
        Files.write(destinationPath, file.getBytes(), StandardOpenOption.CREATE);
    }

    /**
     * Lê o conteúdo de um arquivo salvo no diretório.
     * 
     * @param fileName Nome do arquivo a ser lido.
     * @return Conteúdo do arquivo em bytes.
     * @throws IOException Se ocorrer algum erro ao ler o arquivo.
     */
    public byte[] read(String fileName) throws IOException {
        Path filePath = getStorageDirectory().resolve(sanitizeFileName(fileName));

        if (!Files.exists(filePath)) {
            throw new IOException("Arquivo não encontrado: " + fileName);
        }

        return Files.readAllBytes(filePath);
    }

    /**
     * Verifica se o arquivo existe no diretório de armazenamento.
     * 
     * @param fileName Nome do arquivo a ser verificado.
     * @return true se o arquivo existir, false caso contrário.
     * @throws IOException 
     */
    public boolean fileExists(String fileName) throws IOException {
        Path filePath = getStorageDirectory().resolve(sanitizeFileName(fileName));
        return Files.exists(filePath);
    }

    /**
     * Valida o arquivo enviado para garantir que ele é aceitável.
     * 
     * @param file Arquivo Multipart a ser validado.
     * @throws IOException Se o arquivo não for válido.
     */
    private void validateFile(MultipartFile file) throws IOException {
        Objects.requireNonNull(file, "Arquivo não pode ser nulo");

        if (file.isEmpty()) {
            throw new IOException("O arquivo enviado está vazio.");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IOException("O arquivo excede o tamanho máximo permitido de 10 MB.");
        }
    }

    /**
     * Garante que o diretório de armazenamento existe e retorna seu Path.
     * 
     * @return O Path do diretório de armazenamento.
     * @throws IOException Se ocorrer algum erro ao acessar o diretório.
     */
    private Path getStorageDirectory() throws IOException {
        Path storageDir = Paths.get(STORAGE_PATH);
        if (!Files.exists(storageDir)) {
            Files.createDirectories(storageDir);
        }
        return storageDir;
    }

    /**
     * Sanitiza o nome do arquivo para evitar problemas de segurança.
     * Remove qualquer potencial tentativa de path traversal.
     * 
     * @param fileName Nome do arquivo original.
     * @return Nome do arquivo sanitizado.
     */
    private String sanitizeFileName(String fileName) {
        return Paths.get(fileName).getFileName().toString();
    }
}
