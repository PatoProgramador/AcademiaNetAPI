package com.academianet.demo.service;

import com.academianet.demo.common.PasswordHasher;
import com.academianet.demo.common.RoleCodes;
import com.academianet.demo.dto.AuthResponse;
import com.academianet.demo.dto.LoginRequest;
import com.academianet.demo.entity.User;
import com.academianet.demo.exception.UnauthorizedException;
import com.academianet.demo.repository.UserRepository;
import com.academianet.demo.security.JwtService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository, JwtService jwtService) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
    }

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

        String token = jwtService.generateToken(user);

        return new AuthResponse(
                token,
                "Bearer",
                jwtService.getExpirationMs(),
                user.getId(),
                user.getFirstName() + " " + user.getLastName(),
                user.getEmail(),
                RoleCodes.toFront(user.getRole().getCode()),
                user.getCompany().getId(),
                user.getCompany().getName()
        );
    }
}
