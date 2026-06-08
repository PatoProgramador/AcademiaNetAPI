package com.academiaNetAPI.demo.dto;

import java.util.UUID;

/**
 * Respuesta de login. role usa los valores que espera el front:
 * "estudiante" | "profesor" | "admin".
 */
public record AuthResponse(
        UUID id,
        String name,
        String email,
        String role,
        UUID companyId,
        String companyName
) {}
