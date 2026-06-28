package com.academiaNetAPI.demo.service;

import com.academiaNetAPI.demo.dto.CourseResponse;
import com.academiaNetAPI.demo.entity.Company;
import com.academiaNetAPI.demo.entity.Course;
import com.academiaNetAPI.demo.entity.Enrollment;
import com.academiaNetAPI.demo.entity.Evaluation;
import com.academiaNetAPI.demo.entity.Role;
import com.academiaNetAPI.demo.entity.Subject;
import com.academiaNetAPI.demo.entity.User;
import com.academiaNetAPI.demo.enums.EvaluationType;
import com.academiaNetAPI.demo.repository.CourseRepository;
import com.academiaNetAPI.demo.repository.EnrollmentRepository;
import com.academiaNetAPI.demo.repository.GradeRepository;
import com.academiaNetAPI.demo.support.TestEntities;
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
class CourseServiceTest {

    @Mock CourseRepository courseRepository;
    @Mock EnrollmentRepository enrollmentRepository;
    @Mock GradeRepository gradeRepository;
    @Mock TenantService tenantService;
    @InjectMocks CourseService courseService;

    private Company company;
    private User professor;

    @BeforeEach
    void setUp() {
        company = TestEntities.company("Demo");
        Role profRole = TestEntities.role(company, "PROFESSOR");
        professor = TestEntities.user(company, profRole, "Carlos", "Mendoza", "carlos@test.com");
    }

    @Test
    void list_computesAverageFromGrades() {
        Subject subject = TestEntities.subject(company, "Cálculo", "MAT-101", 6);
        Course course = TestEntities.course(company, subject, professor);
        Enrollment e1 = TestEntities.enrollment(company, null, course, 90);
        Enrollment e2 = TestEntities.enrollment(company, null, course, 90);
        Evaluation ev = TestEntities.evaluation(company, course, "Examen", EvaluationType.EXAM);

        when(tenantService.resolve(null)).thenReturn(company);
        when(courseRepository.findByCompany(company)).thenReturn(List.of(course));
        when(enrollmentRepository.findByCourse(course)).thenReturn(List.of(e1, e2));
        when(gradeRepository.findByEnrollment(e1)).thenReturn(List.of(TestEntities.grade(company, e1, ev, "8.0", true)));
        when(gradeRepository.findByEnrollment(e2)).thenReturn(List.of(TestEntities.grade(company, e2, ev, "9.0", true)));

        List<CourseResponse> res = courseService.list(null);

        assertThat(res).hasSize(1);
        CourseResponse c = res.get(0);
        assertThat(c.code()).isEqualTo("MAT-101");
        assertThat(c.credits()).isEqualTo(6);
        assertThat(c.professor()).isEqualTo("Carlos Mendoza");
        assertThat(c.average()).isEqualByComparingTo("8.5");
        assertThat(c.modality()).isEqualTo("IN_PERSON");
        assertThat(c.status()).isEqualTo("IN_PROGRESS");
    }

    @Test
    void list_nullAverageWhenNoGrades() {
        Subject subject = TestEntities.subject(company, "Física", "FIS-101", 4);
        Course course = TestEntities.course(company, subject, professor);

        when(tenantService.resolve(null)).thenReturn(company);
        when(courseRepository.findByCompany(company)).thenReturn(List.of(course));
        when(enrollmentRepository.findByCourse(course)).thenReturn(List.of());

        assertThat(courseService.list(null).get(0).average()).isNull();
    }

    @Test
    void list_nullProfessorHandled() {
        Subject subject = TestEntities.subject(company, "Ética", "HUM-101", 3);
        Course course = TestEntities.course(company, subject, null);

        when(tenantService.resolve(null)).thenReturn(company);
        when(courseRepository.findByCompany(company)).thenReturn(List.of(course));
        when(enrollmentRepository.findByCourse(course)).thenReturn(List.of());

        assertThat(courseService.list(null).get(0).professor()).isNull();
    }
}
