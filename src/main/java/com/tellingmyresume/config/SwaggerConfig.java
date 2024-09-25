package com.tellingmyresume.config;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;

@Configuration
public class SwaggerConfig {

	@Bean
	OpenAPI customOpenAPI() {
		return new OpenAPI().info(new Info().title("Telling My Resume API")
				.description("Documentação da API para o projeto Telling My Resume").version("1.0.0")
				.license(new License().name("Apache 2.0").url("http://springdoc.org")));
	}

	@Bean
	GroupedOpenApi publicApi() {
		return GroupedOpenApi.builder().group("public").pathsToMatch("/resume/**").build();
	}
}