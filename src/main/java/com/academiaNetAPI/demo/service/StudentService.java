package com.academiaNetAPI.demo.service;

import com.academiaNetAPI.demo.common.RoleCodes;
import com.academiaNetAPI.demo.dto.StudentResponse;
import com.academiaNetAPI.demo.entity.Company;
import com.academiaNetAPI.demo.entity.Enrollment;
import com.academiaNetAPI.demo.entity.Grade;
import com.academiaNetAPI.demo.entity.User;
import com.academiaNetAPI.demo.exception.NotFoundException;
import com.academiaNetAPI.demo.repository.EnrollmentRepository;
import com.academiaNetAPI.demo.repository.GradeRepository;
import com.academiaNetAPI.demo.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.OptionalDouble;
import java.util.UUID;

@Service
public class StudentService {

    private final UserRepository userRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final GradeRepository gradeRepository;
    private final TenantService tenantService;

    public StudentService(UserRepository userRepository, EnrollmentRepository enrollmentRepository,
                          GradeRepository gradeRepository, TenantService tenantService) {
        this.userRepository = userRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.gradeRepository = gradeRepository;
        this.tenantService = tenantService;
    }

    @Transactional(readOnly = true)
    public List<StudentResponse> list(UUID companyId) {
        Company company = tenantService.resolve(companyId);
        return userRepository.findByCompanyAndRole_CodeOrderByFirstNameAsc(company, RoleCodes.STUDENT).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<StudentResponse> listByProfessor(UUID professorId) {
        User professor = userRepository.findById(professorId)
                .orElseThrow(() -> new NotFoundException("Profesor no encontrado: " + professorId));
        Map<UUID, User> uniqueStudents = new LinkedHashMap<>();
        for (Enrollment enrollment : enrollmentRepository.findByCourse_Professor(professor)) {
            uniqueStudents.putIfAbsent(enrollment.getStudent().getId(), enrollment.getStudent());
        }
        return uniqueStudents.values().stream().map(this::toResponse).toList();
    }

    private StudentResponse toResponse(User student) {
        List<Enrollment> enrollments = enrollmentRepository.findByStudent(student);

        OptionalDouble attendance = enrollments.stream()
                .filter(e -> e.getAttendancePercentage() != null)
                .mapToInt(Enrollment::getAttendancePercentage)
                .average();

        List<Grade> grades = gradeRepository.findByEnrollment_Student(student);
        BigDecimal average = null;
        if (!grades.isEmpty()) {
            BigDecimal sum = grades.stream().map(Grade::getValue).reduce(BigDecimal.ZERO, BigDecimal::add);
            average = sum.divide(BigDecimal.valueOf(grades.size()), 1, RoundingMode.HALF_UP);
        }

        String status = (average != null && average.doubleValue() < 7.0) ? "En Riesgo" : "Activo";

        return new StudentResponse(
                student.getId(),
                student.getFirstName() + " " + student.getLastName(),
                student.getDocumentNumber(),
                attendance.isPresent() ? (int) Math.round(attendance.getAsDouble()) : null,
                average,
                status
        );
    }
}
