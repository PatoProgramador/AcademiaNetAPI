package com.academianet.demo.service;

import com.academianet.demo.common.RoleCodes;
import com.academianet.demo.dto.DashboardStats;
import com.academianet.demo.entity.Company;
import com.academianet.demo.entity.Course;
import com.academianet.demo.entity.Enrollment;
import com.academianet.demo.entity.Evaluation;
import com.academianet.demo.entity.Role;
import com.academianet.demo.entity.Subject;
import com.academianet.demo.entity.User;
import com.academianet.demo.enums.CourseStatus;
import com.academianet.demo.enums.EvaluationType;
import com.academianet.demo.repository.CourseRepository;
import com.academianet.demo.repository.GradeRepository;
import com.academianet.demo.repository.UserRepository;
import com.academianet.demo.support.TestEntities;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DashboardServiceTest {

    @Mock UserRepository userRepository;
    @Mock CourseRepository courseRepository;
    @Mock GradeRepository gradeRepository;
    @Mock TenantService tenantService;
    @InjectMocks DashboardService dashboardService;

    private Company company;

    @BeforeEach
    void setUp() {
        company = TestEntities.company("Demo");
    }

    @Test
    void stats_countsAndAveragesCorrectly() {
        Role studentRole = TestEntities.role(company, RoleCodes.STUDENT);
        Role profRole = TestEntities.role(company, RoleCodes.PROFESSOR);
        User s1 = TestEntities.user(company, studentRole, "Ana", "G", "ana@test.com");
        User s2 = TestEntities.user(company, studentRole, "Luis", "R", "luis@test.com");
        User p1 = TestEntities.user(company, profRole, "Carlos", "M", "carlos@test.com");

        Subject subject = TestEntities.subject(company, "Cálculo", "MAT-101", 6);
        Course open = TestEntities.course(company, subject, p1);
        open.setStatus(CourseStatus.IN_PROGRESS);
        Course closed = TestEntities.course(company, subject, p1);
        closed.setStatus(CourseStatus.CLOSED);

        Enrollment enr = TestEntities.enrollment(company, s1, open, 90);
        Evaluation ev = TestEntities.evaluation(company, open, "Examen", EvaluationType.EXAM);

        when(tenantService.resolve(null)).thenReturn(company);
        when(userRepository.findByCompanyOrderByFirstNameAsc(company)).thenReturn(List.of(s1, s2, p1));
        when(userRepository.findByCompanyAndRole_CodeOrderByFirstNameAsc(company, RoleCodes.STUDENT))
                .thenReturn(List.of(s1, s2));
        when(userRepository.findByCompanyAndRole_CodeOrderByFirstNameAsc(company, RoleCodes.PROFESSOR))
                .thenReturn(List.of(p1));
        when(courseRepository.findByCompany(company)).thenReturn(List.of(open, closed));
        when(gradeRepository.findAll()).thenReturn(List.of(
                TestEntities.grade(company, enr, ev, "8.0", true),
                TestEntities.grade(company, enr, ev, "9.0", true)));

        DashboardStats stats = dashboardService.stats(null);

        assertThat(stats.totalUsers()).isEqualTo(3);
        assertThat(stats.totalStudents()).isEqualTo(2);
        assertThat(stats.totalProfessors()).isEqualTo(1);
        assertThat(stats.activeCourses()).isEqualTo(1);
        assertThat(stats.institutionalAverage()).isEqualByComparingTo("8.5");
    }

    @Test
    void stats_nullAverageWhenNoGrades() {
        when(tenantService.resolve(null)).thenReturn(company);
        when(userRepository.findByCompanyOrderByFirstNameAsc(company)).thenReturn(List.of());
        when(userRepository.findByCompanyAndRole_CodeOrderByFirstNameAsc(company, RoleCodes.STUDENT)).thenReturn(List.of());
        when(userRepository.findByCompanyAndRole_CodeOrderByFirstNameAsc(company, RoleCodes.PROFESSOR)).thenReturn(List.of());
        when(courseRepository.findByCompany(company)).thenReturn(List.of());
        when(gradeRepository.findAll()).thenReturn(List.of());

        assertThat(dashboardService.stats(null).institutionalAverage()).isNull();
    }
}
