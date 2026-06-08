package com.academiaNetAPI.demo.dto;

import java.math.BigDecimal;
import java.util.UUID;

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
