package com.academianet.demo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UserPreferencesRequest(
        @NotBlank String theme,
        @NotBlank String language,
        @NotNull Boolean emailNotifications,
        @NotBlank String timezone
) {}
