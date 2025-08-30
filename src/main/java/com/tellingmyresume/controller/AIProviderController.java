package com.tellingmyresume.controller;

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
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/ai")
@Tag(name = "AI Providers", description = "Gerenciamento de provedores de IA")
public class AIProviderController {
    
    private final AIProviderService aiProviderService;
    
    public AIProviderController(AIProviderService aiProviderService) {
        this.aiProviderService = aiProviderService;
    }
    
    @Operation(summary = "Lista provedores disponíveis", 
               description = "Retorna lista de provedores de IA atualmente disponíveis")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de provedores obtida com sucesso")
    })
    @GetMapping("/providers")
    public ResponseEntity<List<String>> getAvailableProviders() {
        List<String> providers = aiProviderService.getAvailableProviders();
        return ResponseEntity.ok(providers);
    }
    
    @Operation(summary = "Status completo do sistema", 
               description = "Retorna status detalhado de todos os provedores de IA")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Status obtido com sucesso")
    })
    @GetMapping("/status")
    public ResponseEntity<ProviderStatusResponse> getSystemStatus() {
        ProviderStatusResponse status = aiProviderService.getSystemStatus();
        return ResponseEntity.ok(status);
    }
    
    @Operation(summary = "Status de provedor específico", 
               description = "Verifica se um provedor específico está disponível")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Status verificado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Provedor não encontrado")
    })
    @GetMapping("/providers/{providerName}/status")
    public ResponseEntity<Map<String, Boolean>> getProviderStatus(
            @Parameter(description = "Nome do provedor (Claude, Gemini)") 
            @PathVariable String providerName) {
        boolean available = aiProviderService.isProviderAvailable(providerName);
        Map<String, Boolean> status = Map.of(providerName, available);
        return ResponseEntity.ok(status);
    }
    
    @Operation(summary = "Provedor preferido", 
               description = "Retorna o provedor preferido do sistema")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Provedor preferido obtido com sucesso"),
        @ApiResponse(responseCode = "503", description = "Nenhum provedor disponível")
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