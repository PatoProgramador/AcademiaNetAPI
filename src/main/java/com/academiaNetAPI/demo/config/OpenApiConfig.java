package com.academiaNetAPI.demo.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "AcademiaNet API",
                version = "1.0.0",
                description = """
                        API REST del sistema de gestión académica multi-tenant AcademiaNet.

                        Autenticación: login simple (sin JWT). El campo `role` se devuelve en el
                        formato del front: `estudiante` | `profesor` | `admin`.

                        Credenciales demo (password `123456`): admin@test.com, profesor@test.com,
                        estudiante@test.com.

                        El parámetro `companyId` es opcional; si se omite se usa la empresa demo.
                        """,
                contact = @Contact(name = "AcademiaNet")
        ),
        servers = {
                @Server(url = "http://localhost:8080", description = "Local")
        }
)
public class OpenApiConfig {
}
