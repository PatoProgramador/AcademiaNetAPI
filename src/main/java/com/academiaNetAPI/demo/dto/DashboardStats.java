package com.academiaNetAPI.demo.dto;

import java.math.BigDecimal;

/** Métricas globales para el dashboard del admin. */
public record DashboardStats(
        long totalUsers,
        long totalStudents,
        long totalProfessors,
        long activeCourses,
        BigDecimal institutionalAverage
) {}
