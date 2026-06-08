package com.academiaNetAPI.demo.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record StudentResponse(
        UUID id,
        String name,
        String code,
        Integer attendance,
        BigDecimal average,
        String status
) {}
