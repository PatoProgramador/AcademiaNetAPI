package com.academianet.demo.security;

import java.util.UUID;

public record JwtPrincipal(
        UUID userId,
        String email,
        String role,
        UUID companyId,
        String name
) {}
