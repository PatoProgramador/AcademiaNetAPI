package com.academianet.demo;

import com.academianet.demo.dto.AuthResponse;
import com.academianet.demo.dto.CourseResponse;
import com.academianet.demo.dto.GradeResponse;
import com.academianet.demo.dto.LoginRequest;
import com.academianet.demo.dto.StudentResponse;
import com.academianet.demo.dto.UserResponse;
import com.academianet.demo.exception.UnauthorizedException;
import com.academianet.demo.service.AuthService;
import com.academianet.demo.service.CourseService;
import com.academianet.demo.service.DashboardService;
import com.academianet.demo.service.GradeService;
import com.academianet.demo.service.StudentService;
import com.academianet.demo.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@TestPropertySource(properties = "academianet.seed.enabled=true")
class SeedSmokeTest {

    @Autowired AuthService authService;
    @Autowired UserService userService;
    @Autowired CourseService courseService;
    @Autowired StudentService studentService;
    @Autowired GradeService gradeService;
    @Autowired DashboardService dashboardService;

    @Test
    void login_mapsRolesToFrontValues() {
        AuthResponse student = authService.login(new LoginRequest("estudiante@test.com", "123456"));
        assertThat(student.role()).isEqualTo("estudiante");
        assertThat(student.name()).isEqualTo("Ana García López");

        assertThat(authService.login(new LoginRequest("profesor@test.com", "123456")).role()).isEqualTo("profesor");
        assertThat(authService.login(new LoginRequest("admin@test.com", "123456")).role()).isEqualTo("admin");
    }

    @Test
    void login_rejectsWrongPassword() {
        assertThatThrownBy(() -> authService.login(new LoginRequest("estudiante@test.com", "nope")))
                .isInstanceOf(UnauthorizedException.class);
    }

    @Test
    void seededCatalogIsAvailable() {
        List<UserResponse> users = userService.list(null);
        assertThat(users).hasSizeGreaterThanOrEqualTo(9);

        List<CourseResponse> courses = courseService.list(null);
        assertThat(courses).hasSize(8);
        assertThat(courses).anyMatch(c -> "MAT-101".equals(c.code()) && c.average() != null);

        List<StudentResponse> students = studentService.list(null);
        assertThat(students).hasSize(8);
        assertThat(students).anyMatch(s -> "En Riesgo".equals(s.status()));

        assertThat(dashboardService.stats(null).totalStudents()).isEqualTo(8);
    }

    @Test
    void studentSeesPublishedGrades() {
        AuthResponse ana = authService.login(new LoginRequest("estudiante@test.com", "123456"));
        List<GradeResponse> grades = gradeService.recentForStudent(ana.id());
        assertThat(grades).isNotEmpty();
        assertThat(grades).allMatch(GradeResponse::published);
    }
}
