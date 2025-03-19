package com.tellingmyresume.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import com.tellingmyresume.model.Resume;
import com.tellingmyresume.repository.ResumeRepository;

@ExtendWith(MockitoExtension.class)
class DatabaseStorageServiceTest {

    @Mock
    private ResumeRepository resumeRepository;

    @InjectMocks
    private DatabaseStorageService storageService;

    private MultipartFile validFile;
    private MultipartFile emptyFile;
    private MultipartFile largeFile;

    @BeforeEach
    void setUp() {
        validFile = new MockMultipartFile(
            "test.pdf",
            "test.pdf",
            "application/pdf",
            "Test content".getBytes()
        );

        emptyFile = new MockMultipartFile(
            "empty.pdf",
            "empty.pdf",
            "application/pdf",
            new byte[0]
        );

        largeFile = new MockMultipartFile(
            "large.pdf",
            "large.pdf",
            "application/pdf",
            "test".getBytes()
        ) {
            @Override
            public long getSize() {
                return 11 * 1024 * 1024; // 11MB
            }
        };
    }

    @Test
    void testSave_ValidFile_SavesSuccessfully() throws IOException {
        // Arrange
        when(resumeRepository.save(any(Resume.class))).thenReturn(new Resume());

        // Act
        storageService.save("test.pdf", validFile);

        // Assert
        verify(resumeRepository, times(1)).save(any(Resume.class));
    }

    @Test
    void testSave_EmptyFile_ThrowsException() {
        // Act & Assert
        Exception exception = assertThrows(IOException.class, () -> {
            storageService.save("empty.pdf", emptyFile);
        });

        assertTrue(exception.getMessage().contains("vazio"));
        verify(resumeRepository, never()).save(any(Resume.class));
    }

    @Test
    void testSave_OversizedFile_ThrowsException() {
        // Act & Assert
        Exception exception = assertThrows(IOException.class, () -> {
            storageService.save("large.pdf", largeFile);
        });

        assertTrue(exception.getMessage().contains("tamanho máximo"));
        verify(resumeRepository, never()).save(any(Resume.class));
    }

    @Test
    void testRead_ExistingFile_ReturnsContent() throws IOException {
        // Arrange
        Resume mockResume = new Resume("test.pdf", "application/pdf", "Test content".getBytes());
        when(resumeRepository.findByFileName("test.pdf")).thenReturn(mockResume);

        // Act
        byte[] result = storageService.read("test.pdf");

        // Assert
        assertArrayEquals("Test content".getBytes(), result);
    }

    @Test
    void testRead_NonexistentFile_ThrowsException() {
        // Arrange
        when(resumeRepository.findByFileName("nonexistent.pdf")).thenReturn(null);

        // Act & Assert
        Exception exception = assertThrows(IOException.class, () -> {
            storageService.read("nonexistent.pdf");
        });

        assertTrue(exception.getMessage().contains("não encontrado"));
    }

    @Test
    void testFileExists_ExistingFile_ReturnsTrue() {
        // Arrange
        when(resumeRepository.existsByFileName("test.pdf")).thenReturn(true);

        // Act
        boolean exists = storageService.fileExists("test.pdf");

        // Assert
        assertTrue(exists);
    }

    @Test
    void testFileExists_NonexistentFile_ReturnsFalse() {
        // Arrange
        when(resumeRepository.existsByFileName("nonexistent.pdf")).thenReturn(false);

        // Act
        boolean exists = storageService.fileExists("nonexistent.pdf");

        // Assert
        assertFalse(exists);
    }
} 