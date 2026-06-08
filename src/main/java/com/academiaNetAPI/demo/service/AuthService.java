package com.academiaNetAPI.demo.service;

import com.academiaNetAPI.demo.common.PasswordHasher;
import com.academiaNetAPI.demo.common.RoleCodes;
import com.academiaNetAPI.demo.dto.AuthResponse;
import com.academiaNetAPI.demo.dto.LoginRequest;
import com.academiaNetAPI.demo.entity.User;
import com.academiaNetAPI.demo.exception.UnauthorizedException;
import com.academiaNetAPI.demo.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;

@Service
public class AuthService {

    private final UserRepository userRepository;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /** Login simple (sin JWT): valida email + password y devuelve el usuario. */
    @Transactional
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findFirstByEmailIgnoreCase(request.email().trim())
                .orElseThrow(() -> new UnauthorizedException("Correo electrónico o contraseña incorrectos."));

        if (!user.isActive()) {
            throw new UnauthorizedException("La cuenta está inactiva.");
        }
        if (!PasswordHasher.matches(request.password(), user.getPasswordHash())) {
            throw new UnauthorizedException("Correo electrónico o contraseña incorrectos.");
        }

        user.setLastLogin(OffsetDateTime.now());

        return new AuthResponse(
                user.getId(),
                user.getFirstName() + " " + user.getLastName(),
                user.getEmail(),
                RoleCodes.toFront(user.getRole().getCode()),
                user.getCompany().getId(),
                user.getCompany().getName()
        );
    }
}
