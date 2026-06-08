package com.academiaNetAPI.demo.dto;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Espejo de la fila de "Gestión de Alumnos" del profesor:
 * { id, name, code(=matrícula), attendance, average, status }.
 */
public record StudentResponse(
        UUID id,
        String name,
        String code,
        Integer attendance,
        BigDecimal average,
        String status
) {}
