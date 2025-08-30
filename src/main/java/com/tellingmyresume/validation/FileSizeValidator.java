package com.tellingmyresume.validation;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class FileSizeValidator implements ConstraintValidator<FileSize, MultipartFile> {

    private long maxSizeInBytes;

    @Override
    public void initialize(FileSize constraintAnnotation) {
        this.maxSizeInBytes = constraintAnnotation.max() * 1024 * 1024; // Convert MB to bytes
    }

    @Override
    public boolean isValid(MultipartFile file, ConstraintValidatorContext context) {
        if (file == null || file.isEmpty()) {
            return true; // Let @NotNull handle null validation
        }

        return file.getSize() <= maxSizeInBytes;
    }
}