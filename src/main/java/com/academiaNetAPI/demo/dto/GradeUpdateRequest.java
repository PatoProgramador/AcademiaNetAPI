package com.academiaNetAPI.demo.dto;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

/** Actualización de una nota desde el panel del profesor. */
public record GradeUpdateRequest(
        @NotNull BigDecimal value,
        Boolean published
) {}
