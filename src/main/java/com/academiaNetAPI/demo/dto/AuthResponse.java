package com.academiaNetAPI.demo.dto;

import java.util.UUID;

public record AuthResponse(
        UUID id,
        String name,
        String email,
        String role,
        UUID companyId,
        String companyName
) {}
