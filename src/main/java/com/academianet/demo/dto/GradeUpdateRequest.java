package com.academianet.demo.dto;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record GradeUpdateRequest(
        @NotNull BigDecimal value,
        Boolean published
) {}
