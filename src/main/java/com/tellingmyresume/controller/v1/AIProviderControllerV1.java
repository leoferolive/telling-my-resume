package com.tellingmyresume.controller.v1;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tellingmyresume.dto.response.ProviderStatusResponse;
import com.tellingmyresume.service.AIProviderService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/ai")
@Tag(name = "AI Providers API v1", description = "Gerenciamento de provedores de IA - Versão 1.0")
public class AIProviderControllerV1 {
    
    private final AIProviderService aiProviderService;
    
    public AIProviderControllerV1(AIProviderService aiProviderService) {
        this.aiProviderService = aiProviderService;
    }
    
    @Operation(
        summary = "Listar provedores disponíveis", 
        description = "Retorna a lista de provedores de IA atualmente operacionais e prontos para uso"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Lista de provedores obtida com sucesso",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(value = "[\"Claude\", \"Gemini\"]")
            )
        )
    })
    @GetMapping("/providers")
    public ResponseEntity<List<String>> getAvailableProviders() {
        List<String> providers = aiProviderService.getAvailableProviders();
        return ResponseEntity.ok(providers);
    }
    
    @Operation(
        summary = "Status completo do sistema", 
        description = "Fornece um panorama detalhado do status de todos os provedores de IA, incluindo provedor preferido e disponibilidade geral"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Status obtido com sucesso",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(value = "{\"providerStatus\":{\"Claude\":true,\"Gemini\":false},\"availableProviders\":[\"Claude\"],\"preferredProvider\":\"Claude\",\"hasAvailableProvider\":true,\"checkedAt\":\"2025-08-30T04:40:00Z\"}")
            )
        )
    })
    @GetMapping("/status")
    public ResponseEntity<ProviderStatusResponse> getSystemStatus() {
        ProviderStatusResponse status = aiProviderService.getSystemStatus();
        return ResponseEntity.ok(status);
    }
    
    @Operation(
        summary = "Status de provedor específico", 
        description = "Verifica se um provedor de IA específico está disponível e operacional"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Status verificado com sucesso",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(value = "{\"Claude\":true}")
            )
        ),
        @ApiResponse(responseCode = "400", description = "Nome do provedor inválido")
    })
    @GetMapping("/providers/{providerName}/status")
    public ResponseEntity<Map<String, Boolean>> getProviderStatus(
            @Parameter(description = "Nome do provedor de IA", example = "Claude") 
            @PathVariable String providerName) {
        boolean available = aiProviderService.isProviderAvailable(providerName);
        Map<String, Boolean> status = Map.of(providerName, available);
        return ResponseEntity.ok(status);
    }
    
    @Operation(
        summary = "Provedor preferido do sistema", 
        description = "Retorna o provedor de IA com maior prioridade no sistema (usado por padrão na análise inteligente)"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Provedor preferido obtido com sucesso",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(value = "{\"preferred\":\"Claude\"}")
            )
        ),
        @ApiResponse(
            responseCode = "503", 
            description = "Nenhum provedor disponível no momento",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(value = "{\"error\":\"Nenhum provedor disponível\"}")
            )
        )
    })
    @GetMapping("/preferred")
    public ResponseEntity<Map<String, String>> getPreferredProvider() {
        String preferred = aiProviderService.getPreferredProvider();
        if (preferred == null) {
            return ResponseEntity.status(503)
                .body(Map.of("error", "Nenhum provedor disponível"));
        }
        return ResponseEntity.ok(Map.of("preferred", preferred));
    }
}