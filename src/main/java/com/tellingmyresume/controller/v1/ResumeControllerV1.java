package com.tellingmyresume.controller.v1;

import com.tellingmyresume.dto.request.ResumeUploadRequest;
import com.tellingmyresume.dto.response.ResumeAnalysisResponse;
import com.tellingmyresume.dto.response.ResumeContentResponse;
import com.tellingmyresume.dto.response.ResumeUploadResponse;
import com.tellingmyresume.service.ResumeAnalysisService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/resume")
@Validated
@Tag(name = "Resume API v1", description = "API para análise de currículos - Versão 1.0")
public class ResumeControllerV1 {

    private final ResumeAnalysisService resumeAnalysisService;
    
    public ResumeControllerV1(ResumeAnalysisService resumeAnalysisService) {
        this.resumeAnalysisService = resumeAnalysisService;
    }
    
    @Operation(
        summary = "Upload de arquivo de currículo",
        description = "Envia um arquivo de currículo (PDF, DOCX ou TXT) para análise posterior",
        requestBody = @RequestBody(
            description = "Arquivo de currículo e metadados opcionais",
            required = true
        )
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Upload realizado com sucesso",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(value = "{\"fileName\":\"curriculum.pdf\",\"message\":\"Upload successful\",\"fileSize\":1024}")
            )
        ),
        @ApiResponse(responseCode = "400", description = "Arquivo inválido ou dados malformados"),
        @ApiResponse(responseCode = "413", description = "Arquivo muito grande (máx 10MB)"),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResumeUploadResponse> uploadResume(@Valid ResumeUploadRequest request) {
        ResumeUploadResponse response = resumeAnalysisService.uploadResume(request);
        return ResponseEntity.ok(response);
    }
    
    @Operation(
        summary = "Obter conteúdo do currículo",
        description = "Recupera o conteúdo textual extraído de um currículo previamente enviado"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Conteúdo recuperado com sucesso",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(value = "{\"fileName\":\"curriculum.pdf\",\"content\":\"João Silva\\nDesenvolvedor Java...\",\"contentType\":\"text/plain\"}")
            )
        ),
        @ApiResponse(responseCode = "404", description = "Currículo não encontrado")
    })
    @GetMapping("/content/{fileName}")
    public ResponseEntity<ResumeContentResponse> getResumeContent(
            @Parameter(description = "Nome do arquivo do currículo") 
            @PathVariable String fileName) {
        ResumeContentResponse response = resumeAnalysisService.getResumeContent(fileName);
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "Análise inteligente de currículo",
        description = "Analisa o currículo usando o melhor provedor de IA disponível com fallback automático"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Análise gerada com sucesso",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(value = "{\"fileName\":\"curriculum.pdf\",\"analysis\":\"Profissional com sólida experiência...\",\"aiProvider\":\"Claude\",\"timestamp\":\"2025-08-30T04:40:00Z\"}")
            )
        ),
        @ApiResponse(responseCode = "404", description = "Currículo não encontrado"),
        @ApiResponse(responseCode = "503", description = "Nenhum serviço de IA disponível")
    })
    @GetMapping("/analyze/{fileName}")
    public ResponseEntity<ResumeAnalysisResponse> analyzeResume(
            @Parameter(description = "Nome do arquivo do currículo") 
            @PathVariable String fileName) {
        ResumeAnalysisResponse response = resumeAnalysisService.analyzeResumeWithBestAvailable(fileName);
        return ResponseEntity.ok(response);
    }
    
    @Operation(
        summary = "Análise com provedor específico",
        description = "Força a análise usando um provedor de IA específico (Claude ou Gemini)"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Análise gerada com sucesso"),
        @ApiResponse(responseCode = "400", description = "Provedor não suportado"),
        @ApiResponse(responseCode = "404", description = "Currículo não encontrado"),
        @ApiResponse(responseCode = "503", description = "Provedor especificado indisponível")
    })
    @GetMapping("/analyze/{fileName}/provider/{providerName}")
    public ResponseEntity<ResumeAnalysisResponse> analyzeWithProvider(
            @Parameter(description = "Nome do arquivo do currículo") 
            @PathVariable String fileName,
            @Parameter(description = "Provedor de IA (Claude, Gemini)", example = "Claude")
            @PathVariable String providerName) {
        ResumeAnalysisResponse response = resumeAnalysisService.analyzeResumeWithProvider(fileName, providerName);
        return ResponseEntity.ok(response);
    }
}