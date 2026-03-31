package com.platform.sosangongin.config;

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
    public OpenAPI openAPI() {
        String securitySchemeName = "Bearer Token";

        return new OpenAPI()
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName, new SecurityScheme()
                                .name(securitySchemeName)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")))
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .info(apiInfo());
    }

    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("1. Public API")
                .pathsToMatch(
                        "/api/v1/auth/**",
                        "/api/v1/token/**",
                        "/api/v1/app/**"
                )
                .build();
    }

    @Bean
    public GroupedOpenApi authenticatedApi() {
        return GroupedOpenApi.builder()
                .group("2. Authenticated API")
                .pathsToMatch("/api/v1/**")
                .pathsToExclude(
                        "/api/v1/auth/**",
                        "/api/v1/token/**",
                        "/api/v1/app/**"
                )
                .build();
    }

    private Info apiInfo() {
        return new Info()
                .title("Platform Service API")
                .description("Platform Service API Documentation")
                .version("1.0.0");
    }
}
