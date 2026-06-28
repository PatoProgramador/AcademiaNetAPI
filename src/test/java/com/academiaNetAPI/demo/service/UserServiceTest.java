package com.academiaNetAPI.demo.service;

import com.academiaNetAPI.demo.common.RoleCodes;
import com.academiaNetAPI.demo.dto.CreateUserRequest;
import com.academiaNetAPI.demo.dto.UpdateUserRequest;
import com.academiaNetAPI.demo.dto.UserResponse;
import com.academiaNetAPI.demo.entity.Company;
import com.academiaNetAPI.demo.entity.Role;
import com.academiaNetAPI.demo.entity.User;
import com.academiaNetAPI.demo.exception.BadRequestException;
import com.academiaNetAPI.demo.exception.NotFoundException;
import com.academiaNetAPI.demo.repository.RoleRepository;
import com.academiaNetAPI.demo.repository.UserRepository;
import com.academiaNetAPI.demo.support.TestEntities;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock UserRepository userRepository;
    @Mock RoleRepository roleRepository;
    @Mock TenantService tenantService;
    @InjectMocks UserService userService;

    private Company company;
    private Role studentRole;

    @BeforeEach
    void setUp() {
        company = TestEntities.company("Demo");
        studentRole = TestEntities.role(company, RoleCodes.STUDENT);
    }

    @Test
    void create_success_splitsNameAndMapsResponse() {
        when(tenantService.resolve(null)).thenReturn(company);
        when(userRepository.existsByCompanyAndEmailIgnoreCase(company, "ana@test.com")).thenReturn(false);
        when(roleRepository.findByCompanyAndCode(company, RoleCodes.STUDENT)).thenReturn(Optional.of(studentRole));
        when(userRepository.save(any(User.class))).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            if (u.getId() == null) u.setId(UUID.randomUUID());
            return u;
        });

        UserResponse res = userService.create(null,
                new CreateUserRequest("Ana García López", "ana@test.com", "estudiante", null));

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        User saved = captor.getValue();
        assertThat(saved.getFirstName()).isEqualTo("Ana");
        assertThat(saved.getLastName()).isEqualTo("García López");
        assertThat(saved.getPasswordHash()).isNotBlank();
        assertThat(res.role()).isEqualTo("estudiante");
        assertThat(res.status()).isEqualTo("Activo");
    }

    @Test
    void create_duplicateEmailThrows_andDoesNotSave() {
        when(tenantService.resolve(null)).thenReturn(company);
        when(userRepository.existsByCompanyAndEmailIgnoreCase(company, "dup@test.com")).thenReturn(true);

        assertThatThrownBy(() -> userService.create(null,
                new CreateUserRequest("X", "dup@test.com", "estudiante", null)))
                .isInstanceOf(BadRequestException.class);
        verify(userRepository, never()).save(any());
    }

    @Test
    void create_unknownRoleStringThrows() {
        when(tenantService.resolve(null)).thenReturn(company);
        when(userRepository.existsByCompanyAndEmailIgnoreCase(company, "x@test.com")).thenReturn(false);

        assertThatThrownBy(() -> userService.create(null,
                new CreateUserRequest("X", "x@test.com", "ninja", null)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void create_roleNotInCompanyThrows() {
        when(tenantService.resolve(null)).thenReturn(company);
        when(userRepository.existsByCompanyAndEmailIgnoreCase(company, "x@test.com")).thenReturn(false);
        when(roleRepository.findByCompanyAndCode(company, RoleCodes.STUDENT)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.create(null,
                new CreateUserRequest("X", "x@test.com", "estudiante", null)))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    void update_changesStatusToInactive() {
        User user = TestEntities.user(company, studentRole, "Ana", "García", "ana@test.com");
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(roleRepository.findByCompanyAndCode(company, RoleCodes.STUDENT)).thenReturn(Optional.of(studentRole));
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        UserResponse res = userService.update(user.getId(),
                new UpdateUserRequest("Ana García", "ana@test.com", "estudiante", "Inactivo"));

        assertThat(res.status()).isEqualTo("Inactivo");
        assertThat(user.isActive()).isFalse();
    }

    @Test
    void update_missingUserThrows() {
        UUID id = UUID.randomUUID();
        when(userRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.update(id,
                new UpdateUserRequest("A", "a@test.com", "estudiante", null)))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void delete_callsRepositoryDelete() {
        User user = TestEntities.user(company, studentRole, "Ana", "García", "ana@test.com");
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        userService.delete(user.getId());

        verify(userRepository).delete(user);
    }

    @Test
    void delete_missingUserThrows() {
        UUID id = UUID.randomUUID();
        when(userRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.delete(id)).isInstanceOf(NotFoundException.class);
    }

    @Test
    void list_mapsAllUsers() {
        User u1 = TestEntities.user(company, studentRole, "Ana", "García", "ana@test.com");
        User u2 = TestEntities.user(company, studentRole, "Luis", "Ramírez", "luis@test.com");
        when(tenantService.resolve(null)).thenReturn(company);
        when(userRepository.findByCompanyOrderByFirstNameAsc(company)).thenReturn(java.util.List.of(u1, u2));

        assertThat(userService.list(null)).hasSize(2)
                .extracting(UserResponse::email)
                .containsExactly("ana@test.com", "luis@test.com");
    }
}
