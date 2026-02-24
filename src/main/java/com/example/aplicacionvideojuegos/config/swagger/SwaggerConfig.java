package com.example.aplicacionvideojuegos.config.swagger;


import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
class SwaggerConfig {

    @Value("${api.version}")
    private String apiVersion;

    private SecurityScheme createAPIKeyScheme() {
        return new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .bearerFormat("JWT")
                .scheme("bearer");
    }

    @Bean
    OpenAPI apiInfo(){
        return new OpenAPI()
                .info(
                        new Info()
                        .title("Aplicacion Videojuegos API")
                        .version("1.0.0")
                        .description("API de ejemplo para clase para la gestion de una aplicacion de videojuegos")

                .termsOfService("http://mariusplz.dev/docs/license/")

                .license(
                        new License()
                        .name("CC BY-NC-SA 4.0")
                        .url("http://mariusplz.dev/docs/license/"))

                .contact(
                        new Contact()
                        .name("Marius Puruguay Lopez")
                        .email("mapl63@educa.madrid.org")
                        .url("http://mariusplz.dev"))
                )

                .externalDocs(
                        new ExternalDocumentation()
                                .description("GitHub API Videojuegos")
                                .url("https://github.com/mapl63/AplicacionVideoJuegos")
                )

                .addSecurityItem(
                        new SecurityRequirement()
                                .addList("Bearer Authentication")
                )

                .components(
                        new Components()
                        .addSecuritySchemes("Bearer Authentication", createAPIKeyScheme())
                );

    }

    @Bean
    GroupedOpenApi httApi(){
        return GroupedOpenApi.builder()
                .group("http")
                .pathsToMatch("/api/" + apiVersion + "/videoJuegos/**")
                .displayName("API Gestion de videojuegos Spring Boot 2025/2026")
                .build();
    }

}
