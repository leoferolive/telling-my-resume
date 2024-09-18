package com.tellingmyresume.config;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.info.Info;



@Configuration
public class SwaggerConfig {
	
    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("telling-my-resume")
                .pathsToMatch("/**")
                .build();
    }

    @Bean
    public Info apiInfo() {
        return new Info()
                .title("Telling My Resume API")
                .description("Documentação da API para o projeto Telling My Resume")
                .version("1.0.0");
    }
	
}
