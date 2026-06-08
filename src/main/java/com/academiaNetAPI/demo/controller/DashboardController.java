package com.academiaNetAPI.demo.controller;

import com.academiaNetAPI.demo.dto.DashboardStats;
import com.academiaNetAPI.demo.service.DashboardService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/stats")
    public DashboardStats stats(@RequestParam(required = false) UUID companyId) {
        return dashboardService.stats(companyId);
    }
}
