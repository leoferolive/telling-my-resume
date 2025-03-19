package com.tellingmyresume.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.stereotype.Component;

import com.tellingmyresume.exception.InvalidResumeException;

@Component
public class FileUtils {
    
    public String extractText(String fileName, byte[] content) throws IOException {
        String extension = getFileExtension(fileName);
        
        switch (extension.toLowerCase()) {
            case "txt":
                return new String(content);
            case "pdf":
                return extractPdfText(content);
            case "docx":
                return extractDocxText(content);
            default:
                throw new InvalidResumeException("Formato de arquivo não suportado: " + extension);
        }
    }
    
    private String extractPdfText(byte[] content) throws IOException {
        try (PDDocument document = PDDocument.load(content)) {
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(document);
        } catch (IOException e) {
            throw new InvalidResumeException("Erro ao processar arquivo PDF: " + e.getMessage());
        }
    }
    
    private String extractDocxText(byte[] content) throws IOException {
        try (XWPFDocument doc = new XWPFDocument(new ByteArrayInputStream(content));
             XWPFWordExtractor extractor = new XWPFWordExtractor(doc)) {
            return extractor.getText();
        } catch (IOException e) {
            throw new InvalidResumeException("Erro ao processar arquivo DOCX: " + e.getMessage());
        }
    }
    
    private String getFileExtension(String fileName) {
        int lastDot = fileName.lastIndexOf('.');
        if (lastDot == -1) {
            throw new InvalidResumeException("Arquivo sem extensão");
        }
        return fileName.substring(lastDot + 1);
    }
} 