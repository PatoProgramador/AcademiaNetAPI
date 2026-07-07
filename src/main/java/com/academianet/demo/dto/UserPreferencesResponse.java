package com.academianet.demo.dto;

import java.time.Instant;
import java.util.UUID;

public record UserPreferencesResponse(
        UUID userId,
        String theme,
        String language,
        boolean emailNotifications,
        String timezone,
        Instant updatedAt
) {}
