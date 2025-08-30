package com.tellingmyresume.validation;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.ConstraintValidatorContext;

@ExtendWith(MockitoExtension.class)
class FileSizeValidatorTest {

    private FileSizeValidator validator;

    @Mock
    private MultipartFile mockFile;

    @Mock
    private ConstraintValidatorContext context;

    @BeforeEach
    void setUp() {
        validator = new FileSizeValidator();
        // Create a simple FileSize implementation for testing
        FileSize fileSize = new FileSize() {
            @Override
            public Class<? extends java.lang.annotation.Annotation> annotationType() {
                return FileSize.class;
            }

            @Override
            public String message() {
                return "File too large";
            }

            @Override
            public Class<?>[] groups() {
                return new Class[0];
            }

            @Override
            public Class<? extends jakarta.validation.Payload>[] payload() {
                return new Class[0];
            }

            @Override
            public long max() {
                return 10L; // 10 MB
            }
        };
        validator.initialize(fileSize);
    }

    @Test
    void testValidFileSize() {
        // Arrange - 5 MB file (under limit)
        when(mockFile.isEmpty()).thenReturn(false);
        when(mockFile.getSize()).thenReturn(5 * 1024 * 1024L);

        // Act
        boolean result = validator.isValid(mockFile, context);

        // Assert
        assertTrue(result);
    }

    @Test
    void testValidFileSizeAtLimit() {
        // Arrange - Exactly 10 MB file (at limit)
        when(mockFile.isEmpty()).thenReturn(false);
        when(mockFile.getSize()).thenReturn(10 * 1024 * 1024L);

        // Act
        boolean result = validator.isValid(mockFile, context);

        // Assert
        assertTrue(result);
    }

    @Test
    void testInvalidFileSize() {
        // Arrange - 15 MB file (over limit)
        when(mockFile.isEmpty()).thenReturn(false);
        when(mockFile.getSize()).thenReturn(15 * 1024 * 1024L);

        // Act
        boolean result = validator.isValid(mockFile, context);

        // Assert
        assertFalse(result);
    }

    @Test
    void testEmptyFile() {
        // Arrange
        when(mockFile.isEmpty()).thenReturn(true);

        // Act
        boolean result = validator.isValid(mockFile, context);

        // Assert
        assertTrue(result); // Empty file is considered valid for size validation
    }

    @Test
    void testNullFile() {
        // Act
        boolean result = validator.isValid(null, context);

        // Assert
        assertTrue(result); // Null file is considered valid for size validation (@NotNull handles null)
    }
}