package com.academiaNetAPI.demo.dto;

import java.util.UUID;

/**
 * Espejo del AppUser del front: { id, name, email, role, status }.
 * role: "estudiante" | "profesor" | "admin".  status: "Activo" | "Inactivo".
 */
public record UserResponse(
        UUID id,
        String name,
        String email,
        String role,
        String status
) {}
