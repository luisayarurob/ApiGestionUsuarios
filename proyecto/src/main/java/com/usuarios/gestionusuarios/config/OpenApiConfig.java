package com.usuarios.gestionusuarios.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración de OpenAPI / Swagger para la documentación de la API REST.
 * Define el esquema de seguridad con JWT Bearer Token.
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API Gestión de Usuarios")
                        .version("1.0.0")
                        .description("API REST para la gestión de usuarios con autenticación JWT"))
                .components(new Components()
                        .addSecuritySchemes("bearer-jwt", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("Ingresa tu token JWT obtenido en /api/auth/login")))
                .addSecurityItem(new SecurityRequirement().addList("bearer-jwt"));
    }
}
