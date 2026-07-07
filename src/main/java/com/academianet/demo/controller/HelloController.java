package com.academianet.demo.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/hello")
@Tag(name = "Hello", description = "Endpoint público de prueba")
public class HelloController {

    @GetMapping("/7-jul")
    @SecurityRequirements
    @Operation(summary = "Saludo público",
            description = "Endpoint público. Devuelve un saludo sin requerir autenticación.")
    public String hello() {
        return "hello-7-jul";
    }
}
