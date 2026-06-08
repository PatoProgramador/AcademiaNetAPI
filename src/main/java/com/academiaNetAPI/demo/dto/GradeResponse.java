package com.academiaNetAPI.demo.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Espejo de "Notas Recientes" del front:
 * { id, activity, subject, type, date, value }.
 */
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
