package com.tellingmyresume.controller;

import java.io.IOException;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tellingmyresume.service.GeminiService;
import com.tellingmyresume.service.ResumeService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
    
    @Operation(summary = "Salva um currículo", description = "Salva o conteúdo de um currículo em um arquivo no servidor")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Currículo salvo com sucesso"),
        @ApiResponse(responseCode = "400", description = "Erro ao salvar currículo")
    })
    @PostMapping("/save")
    public String saveResume(
            @Parameter(description = "Nome do arquivo onde o currículo será salvo") @RequestParam String fileName, 
            @RequestBody String content) throws IOException {
        resumeService.saveResume(fileName, content);
        return "Currículo salvo com sucesso!";
    }
    
    @Operation(summary = "Lê um currículo", description = "Lê o conteúdo de um currículo salvo no servidor")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Currículo lido com sucesso"),
        @ApiResponse(responseCode = "404", description = "Currículo não encontrado")
    })
    @GetMapping("/read/{fileName}")
    public String readResume(
            @Parameter(description = "Nome do arquivo do currículo") @PathVariable String fileName) throws IOException {
        return resumeService.readResume(fileName);
    }
    
    @Operation(summary = "Gera um novo resumo do currículo", description = "Gera uma nova versão de um currículo utilizando a API do Gemini")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Currículo gerado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Currículo não encontrado")
    })
    @GetMapping("/generate/{fileName}")
    public String generateResume(
            @Parameter(description = "Nome do arquivo do currículo") @PathVariable String fileName) throws IOException {
        String resumeContent = resumeService.readResume(fileName);
        return geminiService.generateResume(resumeContent);
    }
    
}
