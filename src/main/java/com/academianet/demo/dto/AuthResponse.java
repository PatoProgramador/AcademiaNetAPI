package com.academianet.demo.dto;

import java.util.UUID;

public record AuthResponse(
        String token,
        String tokenType,
        long expiresInMs,
        UUID id,
        String name,
        String email,
        String role,
        UUID companyId,
        String companyName
) {}
