package com.tellingmyresume.controller;

import com.tellingmyresume.dto.request.ResumeUploadRequest;
import com.tellingmyresume.dto.response.ResumeAnalysisResponse;
import com.tellingmyresume.dto.response.ResumeContentResponse;
import com.tellingmyresume.dto.response.ResumeUploadResponse;
import com.tellingmyresume.exception.AIServiceException;
import com.tellingmyresume.exception.ResumeNotFoundException;
import com.tellingmyresume.exception.ResumeStorageException;
import com.tellingmyresume.service.ResumeAnalysisService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/resume")
@Validated
public class ResumeController {

    private final ResumeAnalysisService resumeAnalysisService;
    
    public ResumeController(ResumeAnalysisService resumeAnalysisService) {
        this.resumeAnalysisService = resumeAnalysisService;
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
	public ResponseEntity<ResumeUploadResponse> uploadResume(@Valid ResumeUploadRequest request) {
	    try {
	        ResumeUploadResponse response = resumeAnalysisService.uploadResume(request);
	        return ResponseEntity.ok(response);
	    } catch (ResumeStorageException e) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	            .body(new ResumeUploadResponse(
	                request.getFile().getOriginalFilename(),
	                "Erro ao salvar o arquivo: " + e.getMessage()
	            ));
	    }
	}
    
    @Operation(summary = "Lê um currículo", description = "Lê o conteúdo de um currículo salvo no servidor")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Currículo lido com sucesso",
                     content = @Content(examples = @ExampleObject(value = "{\"mensagem\": \"Currículo lido com sucesso!\", \"data\": \"...conteúdo do currículo...\"}"))),
        @ApiResponse(responseCode = "404", description = "Currículo não encontrado")
    })
    @GetMapping("/read/{fileName}")
    public ResponseEntity<ResumeContentResponse> readResume(@Parameter(description = "Nome do arquivo do currículo") 
                                             @PathVariable String fileName) {
        try {
            ResumeContentResponse response = resumeAnalysisService.getResumeContent(fileName);
            return ResponseEntity.ok(response);
        } catch (ResumeNotFoundException e) {
            throw e; // Let GlobalExceptionHandler handle this
        }
    }

    @Operation(summary = "Gera um novo resumo do currículo", description = "Gera uma nova versão de um currículo utilizando a API do Gemini")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Currículo gerado com sucesso",
                     content = @Content(examples = @ExampleObject(value = "{\"mensagem\": \"Currículo gerado com sucesso!\"}"))),
        @ApiResponse(responseCode = "404", description = "Currículo não encontrado")
    })
    @GetMapping("/generate/{fileName}")
    public ResponseEntity<ResumeAnalysisResponse> generateResume(@Parameter(description = "Nome do arquivo do currículo") 
	@PathVariable String fileName) throws ResumeNotFoundException, AIServiceException {
		ResumeAnalysisResponse response = resumeAnalysisService.analyzeResumeWithProvider(fileName, "Gemini");
		return ResponseEntity.ok(response);
	}
    
    @Operation(summary = "Gera um novo resumo do currículo usando o Claude", description = "Gera uma nova versão de um currículo utilizando o modelo Claude")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Resumo gerado com sucesso",
                     content = @Content(examples = @ExampleObject(value = "{\"mensagem\": \"Resumo gerado com sucesso!\"}"))),
        @ApiResponse(responseCode = "404", description = "Currículo não encontrado")
    })
    @GetMapping("/generateClaude/{fileName}")
    public ResponseEntity<ResumeAnalysisResponse> generateResumeWithClaude(@Parameter(description = "Nome do arquivo do currículo") 
                                                           @PathVariable String fileName) 
            throws ResumeNotFoundException, AIServiceException {
        ResumeAnalysisResponse response = resumeAnalysisService.analyzeResumeWithProvider(fileName, "Claude");
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "Analisa currículo com melhor provedor disponível", 
               description = "Analisa o currículo usando o melhor provedor de IA disponível")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Análise gerada com sucesso"),
        @ApiResponse(responseCode = "404", description = "Currículo não encontrado"),
        @ApiResponse(responseCode = "503", description = "Nenhum serviço de IA disponível")
    })
    @GetMapping("/analyze/{fileName}")
    public ResponseEntity<ResumeAnalysisResponse> analyzeResumeWithBestProvider(
            @Parameter(description = "Nome do arquivo do currículo") 
            @PathVariable String fileName) throws ResumeNotFoundException, AIServiceException {
        ResumeAnalysisResponse response = resumeAnalysisService.analyzeResumeWithBestAvailable(fileName);
        return ResponseEntity.ok(response);
    }
    
}