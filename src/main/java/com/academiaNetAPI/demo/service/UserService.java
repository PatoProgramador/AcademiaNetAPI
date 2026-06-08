package com.academiaNetAPI.demo.service;

import com.academiaNetAPI.demo.common.PasswordHasher;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class UserService {

    private static final String DEFAULT_PASSWORD = "123456";

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final TenantService tenantService;

    public UserService(UserRepository userRepository, RoleRepository roleRepository, TenantService tenantService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.tenantService = tenantService;
    }

    @Transactional(readOnly = true)
    public List<UserResponse> list(UUID companyId) {
        Company company = tenantService.resolve(companyId);
        return userRepository.findByCompanyOrderByFirstNameAsc(company).stream()
                .map(UserService::toResponse)
                .toList();
    }

    @Transactional
    public UserResponse create(UUID companyId, CreateUserRequest request) {
        Company company = tenantService.resolve(companyId);
        if (userRepository.existsByCompanyAndEmailIgnoreCase(company, request.email().trim())) {
            throw new BadRequestException("Ya existe un usuario con ese correo en la empresa.");
        }
        Role role = resolveRole(company, request.role());

        User user = new User();
        user.setCompany(company);
        user.setRole(role);
        applyName(user, request.name());
        user.setEmail(request.email().trim());
        String password = (request.password() == null || request.password().isBlank())
                ? DEFAULT_PASSWORD : request.password();
        user.setPasswordHash(PasswordHasher.hash(password));
        user.setActive(true);

        return toResponse(userRepository.save(user));
    }

    @Transactional
    public UserResponse update(UUID id, UpdateUserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado: " + id));
        applyName(user, request.name());
        user.setEmail(request.email().trim());
        user.setRole(resolveRole(user.getCompany(), request.role()));
        if (request.status() != null) {
            user.setActive(!"Inactivo".equalsIgnoreCase(request.status()));
        }
        return toResponse(userRepository.save(user));
    }

    @Transactional
    public void delete(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado: " + id));
        userRepository.delete(user); // soft-delete (@SQLDelete)
    }

    private Role resolveRole(Company company, String frontRole) {
        String code = RoleCodes.fromFront(frontRole);
        return roleRepository.findByCompanyAndCode(company, code)
                .orElseThrow(() -> new BadRequestException("El rol '" + frontRole + "' no existe en la empresa."));
    }

    /** Divide "Nombre Apellido(s)" en first_name / last_name. */
    private void applyName(User user, String fullName) {
        String trimmed = fullName.trim().replaceAll("\\s+", " ");
        int idx = trimmed.indexOf(' ');
        if (idx < 0) {
            user.setFirstName(trimmed);
            user.setLastName("");
        } else {
            user.setFirstName(trimmed.substring(0, idx));
            user.setLastName(trimmed.substring(idx + 1));
        }
    }

    public static UserResponse toResponse(User user) {
        String name = (user.getLastName() == null || user.getLastName().isBlank())
                ? user.getFirstName()
                : user.getFirstName() + " " + user.getLastName();
        return new UserResponse(
                user.getId(),
                name,
                user.getEmail(),
                RoleCodes.toFront(user.getRole().getCode()),
                user.isActive() ? "Activo" : "Inactivo"
        );
    }
}
