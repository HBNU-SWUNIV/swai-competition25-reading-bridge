package com.dyslexia.dyslexia.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        SecurityScheme securityScheme = new SecurityScheme().type(SecurityScheme.Type.HTTP)
            .scheme("bearer").bearerFormat("JWT");

        return new OpenAPI().components(
            new Components().addSecuritySchemes("bearerAuth", securityScheme)).info(
            new Info().title("Dyslexia API").description("API for Dyslexia application")
                .version("1.0.0")).addSecurityItem(new SecurityRequirement().addList("bearerAuth"));
    }

    @Bean
    public GroupedOpenApi apiGroup() {
        return GroupedOpenApi.builder().group("api")
            .packagesToScan("com.dyslexia.dyslexia.controller")
            .pathsToExclude("/oauth2/**", "/login/**").build();
    }
}