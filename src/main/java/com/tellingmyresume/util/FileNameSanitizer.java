package com.tellingmyresume.util;

import java.util.regex.Pattern;

public class FileNameSanitizer {

    private static final Pattern INVALID_CHARS = Pattern.compile("[^a-zA-Z0-9._\\-\\s]");
    private static final Pattern MULTIPLE_SPACES = Pattern.compile("\\s+");
    private static final Pattern MULTIPLE_DOTS = Pattern.compile("\\.{2,}");
    
    public static String sanitize(String fileName) {
        if (fileName == null || fileName.trim().isEmpty()) {
            return "unnamed_file";
        }

        // Remove invalid characters
        String sanitized = INVALID_CHARS.matcher(fileName).replaceAll("");
        
        // Replace multiple spaces with single space
        sanitized = MULTIPLE_SPACES.matcher(sanitized).replaceAll("_");
        
        // Replace multiple dots with single dot
        sanitized = MULTIPLE_DOTS.matcher(sanitized).replaceAll(".");
        
        // Remove leading/trailing spaces and dots
        sanitized = sanitized.replaceAll("^[.\\s]+|[.\\s]+$", "");
        
        // Ensure the filename is not empty after sanitization
        if (sanitized.isEmpty()) {
            return "unnamed_file";
        }
        
        // Limit length
        if (sanitized.length() > 255) {
            String extension = getFileExtension(sanitized);
            String nameWithoutExtension = sanitized.substring(0, sanitized.length() - extension.length() - 1);
            int maxNameLength = 255 - extension.length() - 1; // -1 for the dot
            if (maxNameLength > 0) {
                sanitized = nameWithoutExtension.substring(0, Math.min(nameWithoutExtension.length(), maxNameLength)) + "." + extension;
            } else {
                sanitized = sanitized.substring(0, 255);
            }
        }
        
        return sanitized;
    }
    
    private static String getFileExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex == -1 || lastDotIndex == fileName.length() - 1) {
            return "";
        }
        return fileName.substring(lastDotIndex + 1);
    }
}