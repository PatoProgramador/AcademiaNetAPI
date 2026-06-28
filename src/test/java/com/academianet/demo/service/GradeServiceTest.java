package com.academianet.demo.service;

import com.academianet.demo.dto.GradeResponse;
import com.academianet.demo.dto.GradeUpdateRequest;
import com.academianet.demo.entity.Company;
import com.academianet.demo.entity.Course;
import com.academianet.demo.entity.Enrollment;
import com.academianet.demo.entity.Evaluation;
import com.academianet.demo.entity.Grade;
import com.academianet.demo.entity.Role;
import com.academianet.demo.entity.Subject;
import com.academianet.demo.entity.User;
import com.academianet.demo.enums.EvaluationType;
import com.academianet.demo.exception.NotFoundException;
import com.academianet.demo.repository.GradeRepository;
import com.academianet.demo.repository.UserRepository;
import com.academianet.demo.support.TestEntities;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GradeServiceTest {

    @Mock GradeRepository gradeRepository;
    @Mock UserRepository userRepository;
    @InjectMocks GradeService gradeService;

    private Company company;
    private User studentUser;
    private Enrollment enrollment;
    private Evaluation evaluation;

    @BeforeEach
    void setUp() {
        company = TestEntities.company("Demo");
        Role role = TestEntities.role(company, "STUDENT");
        studentUser = TestEntities.user(company, role, "Ana", "García", "ana@test.com");
        Subject subject = TestEntities.subject(company, "Cálculo", "MAT-101", 6);
        Course course = TestEntities.course(company, subject, null);
        enrollment = TestEntities.enrollment(company, studentUser, course, 90);
        evaluation = TestEntities.evaluation(company, course, "Examen Parcial", EvaluationType.EXAM);
    }

    @Test
    void recentForStudent_returnsPublishedSortedByDateDesc() {
        Grade older = TestEntities.grade(company, enrollment, evaluation, "7.0", true);
        older.setRecordDate(OffsetDateTime.now().minusDays(5));
        Grade newer = TestEntities.grade(company, enrollment, evaluation, "9.0", true);
        newer.setRecordDate(OffsetDateTime.now());

        when(userRepository.findById(studentUser.getId())).thenReturn(Optional.of(studentUser));
        when(gradeRepository.findByEnrollment_StudentAndPublishedTrue(studentUser))
                .thenReturn(List.of(older, newer));

        List<GradeResponse> res = gradeService.recentForStudent(studentUser.getId());

        assertThat(res).hasSize(2);
        assertThat(res.get(0).value()).isEqualByComparingTo("9.0");
        assertThat(res.get(0).subject()).isEqualTo("Cálculo");
        assertThat(res.get(0).type()).isEqualTo("EXAM");
        assertThat(res).allMatch(GradeResponse::published);
    }

    @Test
    void recentForStudent_missingStudentThrows() {
        UUID id = UUID.randomUUID();
        when(userRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> gradeService.recentForStudent(id)).isInstanceOf(NotFoundException.class);
    }

    @Test
    void update_changesValueAndPublished() {
        Grade grade = TestEntities.grade(company, enrollment, evaluation, "5.0", false);
        when(gradeRepository.findById(grade.getId())).thenReturn(Optional.of(grade));
        when(gradeRepository.save(any(Grade.class))).thenAnswer(inv -> inv.getArgument(0));

        GradeResponse res = gradeService.update(grade.getId(),
                new GradeUpdateRequest(new BigDecimal("9.8"), true));

        assertThat(res.value()).isEqualByComparingTo("9.8");
        assertThat(res.published()).isTrue();
        assertThat(grade.getRecordDate()).isNotNull();
    }

    @Test
    void update_keepsPublishedWhenNull() {
        Grade grade = TestEntities.grade(company, enrollment, evaluation, "5.0", true);
        when(gradeRepository.findById(grade.getId())).thenReturn(Optional.of(grade));
        when(gradeRepository.save(any(Grade.class))).thenAnswer(inv -> inv.getArgument(0));

        GradeResponse res = gradeService.update(grade.getId(),
                new GradeUpdateRequest(new BigDecimal("6.0"), null));

        assertThat(res.value()).isEqualByComparingTo("6.0");
        assertThat(res.published()).isTrue();
    }

    @Test
    void update_missingGradeThrows() {
        UUID id = UUID.randomUUID();
        when(gradeRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> gradeService.update(id, new GradeUpdateRequest(new BigDecimal("5.0"), true)))
                .isInstanceOf(NotFoundException.class);
    }
}
