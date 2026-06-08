package com.academiaNetAPI.demo.dto;

import java.math.BigDecimal;

public record DashboardStats(
        long totalUsers,
        long totalStudents,
        long totalProfessors,
        long activeCourses,
        BigDecimal institutionalAverage
) {}
