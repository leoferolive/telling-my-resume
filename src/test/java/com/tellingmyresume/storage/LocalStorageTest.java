package com.tellingmyresume.storage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

class LocalStorageTest {

    private LocalStorage localStorage;
    
    @BeforeEach
    void setUp() {
        localStorage = new LocalStorage();
    }
    
    @Test
    void testSave_ValidFile_SavesSuccessfully() throws IOException {
        // Arrange
        String fileName = "test-valid-save.pdf";
        byte[] content = "Test PDF content".getBytes();
        MultipartFile file = new MockMultipartFile("file", fileName, "application/pdf", content);
        
        // Garantir que o diretório resumes existe
        Files.createDirectories(Paths.get("resumes"));
        
        // Act
        localStorage.save(fileName, file);
        
        // Assert
        Path expectedPath = Paths.get("resumes", fileName);
        assertTrue(Files.exists(expectedPath));
        assertArrayEquals(content, Files.readAllBytes(expectedPath));
        
        // Limpeza após o teste
        Files.deleteIfExists(expectedPath);
    }
    
    @Test
    void testSave_EmptyFile_ThrowsException() {
        // Arrange
        String fileName = "empty.pdf";
        MultipartFile emptyFile = new MockMultipartFile("file", fileName, "application/pdf", new byte[0]);
        
        // Act & Assert
        Exception exception = assertThrows(IOException.class, () -> {
            localStorage.save(fileName, emptyFile);
        });
        
        assertTrue(exception.getMessage().contains("vazio"));
    }
    
    @Test
    void testSave_NullFile_ThrowsException() {
        // Arrange
        String fileName = "null-file.pdf";
        
        // Act & Assert
        Exception exception = assertThrows(NullPointerException.class, () -> {
            localStorage.save(fileName, null);
        });
        
        // A mensagem pode variar, mas estamos testando o tipo de exceção
    }
    
    @Test
    void testSave_OversizedFile_ThrowsException() throws Exception {
        // Arrange
        String fileName = "large.pdf";
        
        // Create an oversized MockMultipartFile that reports a size larger than the limit
        // but actually contains small amount of data (to avoid memory issues in tests)
        MockMultipartFile largeFile = new MockMultipartFile("file", fileName, "application/pdf", "test".getBytes()) {
            @Override
            public long getSize() {
                return 11 * 1024 * 1024; // 11MB, exceeding the 10MB limit
            }
        };
        
        // Act & Assert
        Exception exception = assertThrows(IOException.class, () -> {
            localStorage.save(fileName, largeFile);
        });
        
        assertTrue(exception.getMessage().contains("tamanho máximo"));
    }
    
    @Test
    void testSave_FileWithSpecialChars_SanitizesFileName() throws IOException {
        // Arrange
        String maliciousFileName = "../../../etc/passwd";
        byte[] content = "Test content".getBytes();
        MultipartFile file = new MockMultipartFile("file", maliciousFileName, "text/plain", content);
        
        // Garantir que o diretório resumes existe
        Files.createDirectories(Paths.get("resumes"));
        
        // Act
        localStorage.save(maliciousFileName, file);
        
        // Assert
        Path sanitizedPath = Paths.get("resumes", "passwd");
        assertTrue(Files.exists(sanitizedPath));
        // Verify that the attack path does not exist
        assertFalse(Files.exists(Paths.get("../../../etc/passwd")));
        
        // Limpeza após o teste
        Files.deleteIfExists(sanitizedPath);
    }
    
    @Test
    void testRead_ExistingFile_ReturnsContent() throws IOException {
        // Arrange
        String fileName = "test-existing.txt";
        String content = "This is test content";
        
        // Garantir que o diretório resumes existe
        Files.createDirectories(Paths.get("resumes"));
        
        // Criar arquivo para o teste
        Path filePath = Paths.get("resumes", fileName);
        Files.write(filePath, content.getBytes());
        
        // Act
        byte[] result = localStorage.read(fileName);
        
        // Assert
        assertEquals(content, new String(result));
        
        // Limpeza após o teste
        Files.deleteIfExists(filePath);
    }
    
    @Test
    void testRead_NonexistentFile_ThrowsException() {
        // Arrange
        String nonExistentFile = "test-not-found.txt";
        
        // Act & Assert
        Exception exception = assertThrows(IOException.class, () -> {
            localStorage.read(nonExistentFile);
        });
        
        assertTrue(exception.getMessage().contains("não encontrado"));
    }
    
    @Test
    void testFileExists_ExistingFile_ReturnsTrue() throws IOException {
        // Arrange
        String fileName = "test-check-exists.txt";
        String content = "This is test content";
        
        // Garantir que o diretório resumes existe
        Files.createDirectories(Paths.get("resumes"));
        
        // Criar arquivo para o teste
        Path filePath = Paths.get("resumes", fileName);
        Files.write(filePath, content.getBytes());
        
        // Act
        boolean exists = localStorage.fileExists(fileName);
        
        // Assert
        assertTrue(exists);
        
        // Limpeza após o teste
        Files.deleteIfExists(filePath);
    }
    
    @Test
    void testFileExists_NonexistentFile_ReturnsFalse() throws IOException {
        // Arrange
        String nonExistentFile = "test-does-not-exist.txt";
        
        // Act
        boolean exists = localStorage.fileExists(nonExistentFile);
        
        // Assert
        assertFalse(exists);
    }
    
    @Test
    void testSave_CreatesStorageDirectoryIfNotExists() throws IOException {
        // Arrange
        String fileName = "test-create-dir.txt";
        byte[] content = "Test content".getBytes();
        MultipartFile file = new MockMultipartFile("file", fileName, "text/plain", content);
        
        // Remover o diretório se existir
        Files.deleteIfExists(Paths.get("resumes", fileName));
        try {
            Files.deleteIfExists(Paths.get("resumes"));
        } catch (IOException e) {
            // Ignora, pois pode não estar vazio
        }
        
        // Act
        localStorage.save(fileName, file);
        
        // Assert
        assertTrue(Files.exists(Paths.get("resumes")));
        assertTrue(Files.exists(Paths.get("resumes", fileName)));
        
        // Limpeza após o teste
        Files.deleteIfExists(Paths.get("resumes", fileName));
    }
}