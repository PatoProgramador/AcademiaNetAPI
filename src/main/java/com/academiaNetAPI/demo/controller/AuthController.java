package com.academiaNetAPI.demo.controller;

import com.academiaNetAPI.demo.dto.AuthResponse;
import com.academiaNetAPI.demo.dto.LoginRequest;
import com.academiaNetAPI.demo.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Autenticación", description = "Login simple sin JWT")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    @Operation(summary = "Iniciar sesión",
            description = "Valida email + password y devuelve el usuario con su rol "
                    + "(estudiante | profesor | admin). Responde 401 si las credenciales son inválidas.")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }
}
