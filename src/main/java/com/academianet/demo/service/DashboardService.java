package com.academianet.demo.service;

import com.academianet.demo.common.RoleCodes;
import com.academianet.demo.dto.DashboardStats;
import com.academianet.demo.entity.Company;
import com.academianet.demo.entity.Grade;
import com.academianet.demo.enums.CourseStatus;
import com.academianet.demo.repository.CourseRepository;
import com.academianet.demo.repository.GradeRepository;
import com.academianet.demo.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.UUID;

@Service
public class DashboardService {

    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final GradeRepository gradeRepository;
    private final TenantService tenantService;

    public DashboardService(UserRepository userRepository, CourseRepository courseRepository,
                            GradeRepository gradeRepository, TenantService tenantService) {
        this.userRepository = userRepository;
        this.courseRepository = courseRepository;
        this.gradeRepository = gradeRepository;
        this.tenantService = tenantService;
    }

    @Transactional(readOnly = true)
    public DashboardStats stats(UUID companyId) {
        Company company = tenantService.resolve(companyId);

        long totalUsers = userRepository.findByCompanyOrderByFirstNameAsc(company).size();
        long students = userRepository.findByCompanyAndRole_CodeOrderByFirstNameAsc(company, RoleCodes.STUDENT).size();
        long professors = userRepository.findByCompanyAndRole_CodeOrderByFirstNameAsc(company, RoleCodes.PROFESSOR).size();
        long activeCourses = courseRepository.findByCompany(company).stream()
                .filter(c -> c.getStatus() == CourseStatus.OPEN || c.getStatus() == CourseStatus.IN_PROGRESS)
                .count();

        List<Grade> grades = gradeRepository.findAll();
        BigDecimal institutionalAverage = null;
        if (!grades.isEmpty()) {
            BigDecimal sum = grades.stream().map(Grade::getValue).reduce(BigDecimal.ZERO, BigDecimal::add);
            institutionalAverage = sum.divide(BigDecimal.valueOf(grades.size()), 1, RoundingMode.HALF_UP);
        }

        return new DashboardStats(totalUsers, students, professors, activeCourses, institutionalAverage);
    }
}
