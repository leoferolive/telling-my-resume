package com.tellingmyresume.service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.tellingmyresume.exception.InvalidResumeException;
import com.tellingmyresume.exception.ResumeNotFoundException;
import com.tellingmyresume.exception.ResumeStorageException;
import com.tellingmyresume.storage.LocalStorage;

@Service
public class ResumeService {

    private final LocalStorage localStorage;
    
    // Lista de extensões com ordem de prioridade
    private static final List<String> EXTENSIONS_PRIORITY = Arrays.asList("txt", "pdf", "docx");

    public ResumeService(LocalStorage localStorage) {
        this.localStorage = localStorage;
    }
    
	public void saveResume(String originalFilename, MultipartFile file) {
		try {
			localStorage.save(originalFilename, file);
		} catch (IOException e) {
			 throw new ResumeStorageException("Erro ao salvar o currículo: " + originalFilename);
		}
	}
    
    /**
     * Lê o conteúdo de um currículo de um arquivo especificado sem a necessidade de fornecer a extensão.
     * O método verifica se o arquivo existe em um dos formatos suportados (txt, pdf, docx) e o processa.
     *
     * @param fileName O nome do arquivo do currículo (sem a extensão).
     * @return O conteúdo do currículo.
     * @throws IOException              Se houver um erro ao ler o arquivo.
     * @throws ResumeNotFoundException   Se o currículo não for encontrado.
     * @throws InvalidResumeException    Se o formato do arquivo não for suportado.
     */
    public String readResume(String fileName) throws IOException {
        // Busca o arquivo com base no nome e extensão
        String fileWithExtension = findFileWithExtension(fileName);

        if (fileWithExtension == null) {
            throw new ResumeNotFoundException("Currículo não encontrado com o nome: " + fileName);
        }

        // Lê o conteúdo do arquivo encontrado
        byte[] fileContent = localStorage.read(fileWithExtension);
        String extension = getFileExtension(fileWithExtension);

        // Processa o arquivo de acordo com a extensão
        switch (extension.toLowerCase()) {
            case "txt":
                return new String(fileContent); // Apenas converte os bytes para String no caso de TXT
            case "pdf":
                return readPdfFile(fileContent); // Converte PDF em texto
            case "docx":
                return readWordFile(fileContent); // Converte DOCX em texto
            default:
                throw new InvalidResumeException("Formato de arquivo não suportado: " + extension);
        }
    }

    /**
     * Busca por um arquivo que tenha o nome especificado e uma das extensões suportadas.
     *
     * @param fileName O nome do arquivo (sem extensão).
     * @return O nome completo do arquivo com a extensão encontrada, ou null se não encontrado.
     * @throws IOException Se houver erro ao acessar o diretório.
     */
    private String findFileWithExtension(String fileName) throws IOException {
        Path storageDir = Paths.get(LocalStorage.STORAGE_PATH);
        
        // Verifica se o diretório existe
        if (!Files.exists(storageDir)) {
            return null;
        }

        // Itera sobre a lista de extensões em ordem de prioridade
        for (String extension : EXTENSIONS_PRIORITY) {
            Path filePath = storageDir.resolve(fileName + "." + extension);
            if (Files.exists(filePath)) {
                return fileName + "." + extension; // Retorna o primeiro arquivo encontrado
            }
        }

        // Se não encontrar nenhum arquivo com as extensões suportadas
        return null;
    }

    // Método para extrair texto de um PDF
    private String readPdfFile(byte[] fileContent) {
        try (PDDocument document = PDDocument.load(fileContent)) {
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(document);
        } catch (IOException e) {
            throw new InvalidResumeException("Erro ao processar arquivo PDF: " + e.getMessage());
        }
    }

    // Método para extrair texto de um DOCX
    private String readWordFile(byte[] fileContent) {
        try (XWPFDocument doc = new XWPFDocument(new ByteArrayInputStream(fileContent));
				XWPFWordExtractor extractor = new XWPFWordExtractor(doc)) {
            return extractor.getText();
        } catch (IOException e) {
            throw new InvalidResumeException("Erro ao processar arquivo DOCX: " + e.getMessage());
        }
    }

    // Método para obter a extensão do arquivo
    private String getFileExtension(String fileName) {
        return fileName.substring(fileName.lastIndexOf('.') + 1);
    }

}
