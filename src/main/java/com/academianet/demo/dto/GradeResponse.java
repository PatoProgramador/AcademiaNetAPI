package com.academianet.demo.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record GradeResponse(
        UUID id,
        String activity,
        String subject,
        String type,
        LocalDate date,
        BigDecimal value,
        BigDecimal maxValue,
        boolean published
) {}
