package com.academianet.demo.service;

import com.academianet.demo.common.PasswordHasher;
import com.academianet.demo.dto.AuthResponse;
import com.academianet.demo.dto.LoginRequest;
import com.academianet.demo.entity.Company;
import com.academianet.demo.entity.Role;
import com.academianet.demo.entity.User;
import com.academianet.demo.exception.UnauthorizedException;
import com.academianet.demo.repository.UserRepository;
import com.academianet.demo.security.JwtService;
import com.academianet.demo.support.TestEntities;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock UserRepository userRepository;
    @Mock JwtService jwtService;
    @InjectMocks AuthService authService;

    private User student;

    @BeforeEach
    void setUp() {
        Company company = TestEntities.company("Demo University");
        Role role = TestEntities.role(company, "STUDENT");
        student = TestEntities.user(company, role, "Ana", "García López", "estudiante@test.com");
        student.setPasswordHash(PasswordHasher.hash("123456"));
    }

    @Test
    void login_success_returnsTokenAndMappedRole() {
        when(userRepository.findFirstByEmailIgnoreCase("estudiante@test.com")).thenReturn(Optional.of(student));
        when(jwtService.generateToken(student)).thenReturn("jwt-token");
        when(jwtService.getExpirationMs()).thenReturn(86_400_000L);

        AuthResponse res = authService.login(new LoginRequest("estudiante@test.com", "123456"));

        assertThat(res.token()).isEqualTo("jwt-token");
        assertThat(res.tokenType()).isEqualTo("Bearer");
        assertThat(res.expiresInMs()).isEqualTo(86_400_000L);
        assertThat(res.role()).isEqualTo("estudiante");
        assertThat(res.name()).isEqualTo("Ana García López");
        assertThat(res.email()).isEqualTo("estudiante@test.com");
        assertThat(student.getLastLogin()).isNotNull();
    }

    @Test
    void login_trimsEmail() {
        when(userRepository.findFirstByEmailIgnoreCase("estudiante@test.com")).thenReturn(Optional.of(student));
        lenient().when(jwtService.generateToken(any())).thenReturn("jwt");
        lenient().when(jwtService.getExpirationMs()).thenReturn(1L);

        assertThat(authService.login(new LoginRequest("  estudiante@test.com  ", "123456")).token()).isEqualTo("jwt");
    }

    @Test
    void login_wrongPasswordThrows() {
        when(userRepository.findFirstByEmailIgnoreCase("estudiante@test.com")).thenReturn(Optional.of(student));

        assertThatThrownBy(() -> authService.login(new LoginRequest("estudiante@test.com", "wrong")))
                .isInstanceOf(UnauthorizedException.class);
    }

    @Test
    void login_unknownEmailThrows() {
        when(userRepository.findFirstByEmailIgnoreCase("nadie@test.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(new LoginRequest("nadie@test.com", "123456")))
                .isInstanceOf(UnauthorizedException.class);
    }

    @Test
    void login_inactiveUserThrows() {
        student.setActive(false);
        when(userRepository.findFirstByEmailIgnoreCase("estudiante@test.com")).thenReturn(Optional.of(student));

        assertThatThrownBy(() -> authService.login(new LoginRequest("estudiante@test.com", "123456")))
                .isInstanceOf(UnauthorizedException.class);
    }
}
