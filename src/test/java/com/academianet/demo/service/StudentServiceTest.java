package com.academianet.demo.service;

import com.academianet.demo.common.RoleCodes;
import com.academianet.demo.dto.StudentResponse;
import com.academianet.demo.entity.Company;
import com.academianet.demo.entity.Course;
import com.academianet.demo.entity.Enrollment;
import com.academianet.demo.entity.Evaluation;
import com.academianet.demo.entity.Role;
import com.academianet.demo.entity.Subject;
import com.academianet.demo.entity.User;
import com.academianet.demo.enums.EvaluationType;
import com.academianet.demo.exception.NotFoundException;
import com.academianet.demo.repository.EnrollmentRepository;
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
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StudentServiceTest {

    @Mock UserRepository userRepository;
    @Mock EnrollmentRepository enrollmentRepository;
    @Mock GradeRepository gradeRepository;
    @Mock TenantService tenantService;
    @InjectMocks StudentService studentService;

    private Company company;
    private Role studentRole;
    private Course course;

    @BeforeEach
    void setUp() {
        company = TestEntities.company("Demo");
        studentRole = TestEntities.role(company, RoleCodes.STUDENT);
        Subject subject = TestEntities.subject(company, "Cálculo", "MAT-101", 6);
        course = TestEntities.course(company, subject, null);
    }

    private Evaluation evaluation() {
        return TestEntities.evaluation(company, course, "Examen", EvaluationType.EXAM);
    }

    @Test
    void list_lowAverageMarkedEnRiesgo() {
        User ana = TestEntities.student(company, studentRole, "Ana", "García", "2023001");
        Enrollment enr = TestEntities.enrollment(company, ana, course, 70);

        when(tenantService.resolve(null)).thenReturn(company);
        when(userRepository.findByCompanyAndRole_CodeOrderByFirstNameAsc(company, RoleCodes.STUDENT))
                .thenReturn(List.of(ana));
        when(enrollmentRepository.findByStudent(ana)).thenReturn(List.of(enr));
        when(gradeRepository.findByEnrollment_Student(ana))
                .thenReturn(List.of(TestEntities.grade(company, enr, evaluation(), "6.0", true)));

        StudentResponse res = studentService.list(null).get(0);

        assertThat(res.code()).isEqualTo("2023001");
        assertThat(res.average()).isEqualByComparingTo("6.0");
        assertThat(res.attendance()).isEqualTo(70);
        assertThat(res.status()).isEqualTo("En Riesgo");
    }

    @Test
    void list_goodAverageMarkedActivo_andAttendanceAveraged() {
        User luis = TestEntities.student(company, studentRole, "Luis", "Ramírez", "2023002");
        Enrollment e1 = TestEntities.enrollment(company, luis, course, 80);
        Enrollment e2 = TestEntities.enrollment(company, luis, course, 90);

        when(tenantService.resolve(null)).thenReturn(company);
        when(userRepository.findByCompanyAndRole_CodeOrderByFirstNameAsc(company, RoleCodes.STUDENT))
                .thenReturn(List.of(luis));
        when(enrollmentRepository.findByStudent(luis)).thenReturn(List.of(e1, e2));
        when(gradeRepository.findByEnrollment_Student(luis))
                .thenReturn(List.of(TestEntities.grade(company, e1, evaluation(), "8.0", true)));

        StudentResponse res = studentService.list(null).get(0);

        assertThat(res.attendance()).isEqualTo(85);
        assertThat(res.status()).isEqualTo("Activo");
    }

    @Test
    void list_noGradesNullAverageAndActivo() {
        User sofia = TestEntities.student(company, studentRole, "Sofia", "Morales", "2023005");

        when(tenantService.resolve(null)).thenReturn(company);
        when(userRepository.findByCompanyAndRole_CodeOrderByFirstNameAsc(company, RoleCodes.STUDENT))
                .thenReturn(List.of(sofia));
        when(enrollmentRepository.findByStudent(sofia)).thenReturn(List.of());
        when(gradeRepository.findByEnrollment_Student(sofia)).thenReturn(List.of());

        StudentResponse res = studentService.list(null).get(0);

        assertThat(res.average()).isNull();
        assertThat(res.attendance()).isNull();
        assertThat(res.status()).isEqualTo("Activo");
    }

    @Test
    void listByProfessor_deduplicatesStudents() {
        User prof = TestEntities.user(company, TestEntities.role(company, "PROFESSOR"), "Carlos", "Mendoza", "carlos@test.com");
        User ana = TestEntities.student(company, studentRole, "Ana", "García", "2023001");
        Enrollment enr1 = TestEntities.enrollment(company, ana, course, 90);
        Enrollment enr2 = TestEntities.enrollment(company, ana, course, 90); // misma alumna, otro curso

        when(userRepository.findById(prof.getId())).thenReturn(Optional.of(prof));
        when(enrollmentRepository.findByCourse_Professor(prof)).thenReturn(List.of(enr1, enr2));
        when(enrollmentRepository.findByStudent(ana)).thenReturn(List.of(enr1, enr2));
        when(gradeRepository.findByEnrollment_Student(ana)).thenReturn(List.of());

        List<StudentResponse> res = studentService.listByProfessor(prof.getId());

        assertThat(res).hasSize(1);
        assertThat(res.get(0).name()).isEqualTo("Ana García");
    }

    @Test
    void listByProfessor_missingProfessorThrows() {
        UUID id = UUID.randomUUID();
        when(userRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> studentService.listByProfessor(id)).isInstanceOf(NotFoundException.class);
    }
}
