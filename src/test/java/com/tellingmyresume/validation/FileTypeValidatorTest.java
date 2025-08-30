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
class FileTypeValidatorTest {

    private FileTypeValidator validator;

    @Mock
    private MultipartFile mockFile;

    @Mock
    private ConstraintValidatorContext context;

    @BeforeEach
    void setUp() {
        validator = new FileTypeValidator();
        // Create a simple FileType implementation for testing
        FileType fileType = new FileType() {
            @Override
            public Class<? extends java.lang.annotation.Annotation> annotationType() {
                return FileType.class;
            }

            @Override
            public String message() {
                return "Invalid file type";
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
            public String[] value() {
                return new String[]{"pdf", "docx", "txt"};
            }
        };
        validator.initialize(fileType);
    }

    @Test
    void testValidPdfFile() {
        // Arrange
        when(mockFile.isEmpty()).thenReturn(false);
        when(mockFile.getOriginalFilename()).thenReturn("document.pdf");

        // Act
        boolean result = validator.isValid(mockFile, context);

        // Assert
        assertTrue(result);
    }

    @Test
    void testValidDocxFile() {
        // Arrange
        when(mockFile.isEmpty()).thenReturn(false);
        when(mockFile.getOriginalFilename()).thenReturn("document.docx");

        // Act
        boolean result = validator.isValid(mockFile, context);

        // Assert
        assertTrue(result);
    }

    @Test
    void testValidTxtFile() {
        // Arrange
        when(mockFile.isEmpty()).thenReturn(false);
        when(mockFile.getOriginalFilename()).thenReturn("document.txt");

        // Act
        boolean result = validator.isValid(mockFile, context);

        // Assert
        assertTrue(result);
    }

    @Test
    void testInvalidFileExtension() {
        // Arrange
        when(mockFile.isEmpty()).thenReturn(false);
        when(mockFile.getOriginalFilename()).thenReturn("document.jpg");

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
        assertFalse(result);
    }

    @Test
    void testNullFile() {
        // Act
        boolean result = validator.isValid(null, context);

        // Assert
        assertFalse(result);
    }

    @Test
    void testNullFileName() {
        // Arrange
        when(mockFile.isEmpty()).thenReturn(false);
        when(mockFile.getOriginalFilename()).thenReturn(null);

        // Act
        boolean result = validator.isValid(mockFile, context);

        // Assert
        assertFalse(result);
    }

    @Test
    void testFileWithoutExtension() {
        // Arrange
        when(mockFile.isEmpty()).thenReturn(false);
        when(mockFile.getOriginalFilename()).thenReturn("document");

        // Act
        boolean result = validator.isValid(mockFile, context);

        // Assert
        assertFalse(result);
    }

    @Test
    void testCaseInsensitiveValidation() {
        // Arrange
        when(mockFile.isEmpty()).thenReturn(false);
        when(mockFile.getOriginalFilename()).thenReturn("document.PDF");

        // Act
        boolean result = validator.isValid(mockFile, context);

        // Assert
        assertTrue(result);
    }
}