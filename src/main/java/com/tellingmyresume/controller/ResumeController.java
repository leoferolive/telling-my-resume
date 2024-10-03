package com.tellingmyresume.controller;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.tellingmyresume.exception.ResumeNotFoundException;
import com.tellingmyresume.exception.ResumeStorageException;
import com.tellingmyresume.service.GeminiService;
import com.tellingmyresume.service.ResumeService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/resume")
public class ResumeController {

    private final ResumeService resumeService;
    private final GeminiService geminiService;

    public ResumeController(ResumeService resumeService, GeminiService geminiService) {
        this.resumeService = resumeService;
        this.geminiService = geminiService;
    }
    
    @Operation(
    	    summary = "Upload de um arquivo de currículo (PDF, DOCX ou TXT)",
    	    description = "Envia um arquivo de currículo para ser salvo no servidor. O arquivo pode estar nos formatos PDF, DOCX ou TXT.",
    	    requestBody = @RequestBody(),
    	    responses = {
    	        @ApiResponse(responseCode = "200", description = "Arquivo enviado com sucesso"),
    	        @ApiResponse(responseCode = "500", description = "Erro ao salvar o arquivo")
    	    }
    	)
	@PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<String> uploadResume(@RequestPart("file") final MultipartFile file) {
	    try {
	        resumeService.saveResume(file.getOriginalFilename(), file);
	        return ResponseEntity.ok("Arquivo enviado com sucesso!");
	    } catch (ResumeStorageException e) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao salvar o arquivo: " + e.getMessage());
	    }
	}
    
    @Operation(summary = "Lê um currículo", description = "Lê o conteúdo de um currículo salvo no servidor")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Currículo lido com sucesso",
                     content = @Content(examples = @ExampleObject(value = "{\"mensagem\": \"Currículo lido com sucesso!\", \"data\": \"...conteúdo do currículo...\"}"))),
        @ApiResponse(responseCode = "404", description = "Currículo não encontrado")
    })
    @GetMapping("/read/{fileName}")
    public ResponseEntity<String> readResume(@Parameter(description = "Nome do arquivo do currículo") 
                                             @PathVariable String fileName) throws IOException {
        try {
            String resumeContent = resumeService.readResume(fileName);
            return ResponseEntity.status(HttpStatus.OK).body(resumeContent);
        } catch (ResumeNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Currículo não encontrado: " + fileName);
        }
    }

    @Operation(summary = "Gera um novo resumo do currículo", description = "Gera uma nova versão de um currículo utilizando a API do Gemini")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Currículo gerado com sucesso",
                     content = @Content(examples = @ExampleObject(value = "{\"mensagem\": \"Currículo gerado com sucesso!\"}"))),
        @ApiResponse(responseCode = "404", description = "Currículo não encontrado")
    })
    @GetMapping("/generate/{fileName}")
    public ResponseEntity<String> generateResume(@Parameter(description = "Nome do arquivo do currículo") 
	@PathVariable String fileName) {
		try {
			String resumeContent = resumeService.readResume(fileName);
			String generatedResume = geminiService.generateResume(resumeContent);
			return ResponseEntity.status(HttpStatus.OK).body(generatedResume);
		} catch (ResumeNotFoundException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Currículo não encontrado: " + fileName);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Erro ao gerar o currículo: " + e.getMessage());
		}
	}
}