package com.academianet.demo.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "AcademiaNet API",
                version = "1.0.0",
                description = """
                        API REST del sistema de gestión académica multi-tenant AcademiaNet.

                        Autenticación: JWT (Bearer). Haz login en POST /api/auth/login, copia el
                        campo `token` y úsalo con el botón Authorize (o en el header
                        `Authorization: Bearer <token>`). El campo `role` se devuelve en el formato
                        del front: `estudiante` | `profesor` | `admin`.

                        Credenciales demo (password `123456`): admin@test.com, profesor@test.com,
                        estudiante@test.com.

                        El parámetro `companyId` es opcional; si se omite se usa la empresa demo.
                        """,
                contact = @Contact(name = "AcademiaNet")
        ),
        servers = {
                @Server(url = "http://localhost:8080", description = "Local")
        },
        security = @SecurityRequirement(name = "bearerAuth")
)
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT"
)
public class OpenApiConfig {
}
