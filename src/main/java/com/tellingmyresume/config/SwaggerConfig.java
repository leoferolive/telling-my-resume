package com.tellingmyresume.config;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import java.util.List;

@Configuration
public class SwaggerConfig {

	@Bean
	OpenAPI customOpenAPI() {
		return new OpenAPI()
			.info(new Info()
				.title("Telling My Resume API")
				.description("API completa para análise inteligente de currículos usando IA (Claude e Gemini). " +
					"Permite upload, análise e gerenciamento de currículos com suporte a múltiplos provedores de IA.")
				.version("1.0.0")
				.contact(new Contact()
					.name("Telling My Resume Team")
					.email("support@tellingmyresume.com")
					.url("https://github.com/telling-my-resume"))
				.license(new License()
					.name("Apache 2.0")
					.url("https://www.apache.org/licenses/LICENSE-2.0")))
			.servers(List.of(
				new Server()
					.url("http://localhost:8080")
					.description("Servidor de desenvolvimento local"),
				new Server()
					.url("https://api.tellingmyresume.com")
					.description("Servidor de produção")));
	}

	@Bean
	GroupedOpenApi legacyApi() {
		return GroupedOpenApi.builder()
			.group("Legacy API")
			.displayName("Legacy API (sem versão)")
			.pathsToMatch("/resume/**")
			.build();
	}

	@Bean
	GroupedOpenApi v1Api() {
		return GroupedOpenApi.builder()
			.group("API v1")
			.displayName("API v1 - Versão atual")
			.pathsToMatch("/api/v1/**")
			.build();
	}

	@Bean
	GroupedOpenApi aiProvidersApi() {
		return GroupedOpenApi.builder()
			.group("AI Providers")
			.displayName("Gerenciamento de IA")
			.pathsToMatch("/api/v1/ai/**")
			.build();
	}

	@Bean
	GroupedOpenApi resumeAnalysisApi() {
		return GroupedOpenApi.builder()
			.group("Resume Analysis")
			.displayName("Análise de Currículos")
			.pathsToMatch("/api/v1/resume/**")
			.build();
	}
}