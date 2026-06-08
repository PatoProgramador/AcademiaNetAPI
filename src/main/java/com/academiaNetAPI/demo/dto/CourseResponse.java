package com.academiaNetAPI.demo.dto;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Espejo de CURSOS del front: { id, name, code, professor, credits, average, schedule }.
 */
public record CourseResponse(
        UUID id,
        String name,
        String code,
        String professor,
        Integer credits,
        BigDecimal average,
        String schedule,
        String modality,
        String status
) {}
