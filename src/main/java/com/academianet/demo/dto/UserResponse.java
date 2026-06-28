package com.academianet.demo.dto;

import java.util.UUID;

public record UserResponse(
        UUID id,
        String name,
        String email,
        String role,
        String status
) {}
