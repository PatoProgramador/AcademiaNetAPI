package com.academiaNetAPI.demo.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * Alta de usuario desde el panel admin. role: "estudiante" | "profesor" | "admin".
 * password es opcional (si no llega se usa una por defecto para la demo).
 */
public record CreateUserRequest(
        @NotBlank String name,
        @NotBlank @Email String email,
        @NotBlank String role,
        String password
) {}
