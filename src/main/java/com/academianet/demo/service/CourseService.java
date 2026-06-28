package com.academianet.demo.service;

import com.academianet.demo.dto.CourseResponse;
import com.academianet.demo.entity.Company;
import com.academianet.demo.entity.Course;
import com.academianet.demo.entity.Enrollment;
import com.academianet.demo.entity.Grade;
import com.academianet.demo.entity.User;
import com.academianet.demo.repository.CourseRepository;
import com.academianet.demo.repository.EnrollmentRepository;
import com.academianet.demo.repository.GradeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.UUID;

@Service
public class CourseService {

    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final GradeRepository gradeRepository;
    private final TenantService tenantService;

    public CourseService(CourseRepository courseRepository, EnrollmentRepository enrollmentRepository,
                         GradeRepository gradeRepository, TenantService tenantService) {
        this.courseRepository = courseRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.gradeRepository = gradeRepository;
        this.tenantService = tenantService;
    }

    @Transactional(readOnly = true)
    public List<CourseResponse> list(UUID companyId) {
        Company company = tenantService.resolve(companyId);
        return courseRepository.findByCompany(company).stream()
                .map(this::toResponse)
                .toList();
    }

    private CourseResponse toResponse(Course course) {
        User professor = course.getProfessor();
        String professorName = professor == null ? null
                : professor.getFirstName() + " " + professor.getLastName();
        return new CourseResponse(
                course.getId(),
                course.getSubject().getName(),
                course.getSubject().getCode(),
                professorName,
                course.getSubject().getCredits(),
                averageForCourse(course),
                course.getSchedule(),
                course.getModality() == null ? null : course.getModality().name(),
                course.getStatus() == null ? null : course.getStatus().name()
        );
    }

    private BigDecimal averageForCourse(Course course) {
        List<Grade> grades = enrollmentRepository.findByCourse(course).stream()
                .map(gradeRepository::findByEnrollment)
                .flatMap(List::stream)
                .toList();
        if (grades.isEmpty()) return null;
        BigDecimal sum = grades.stream().map(Grade::getValue).reduce(BigDecimal.ZERO, BigDecimal::add);
        return sum.divide(BigDecimal.valueOf(grades.size()), 1, RoundingMode.HALF_UP);
    }
}
