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
    private final UserProfileService userProfileService;

    public AuthService(UserRepository userRepository, JwtService jwtService,
                       UserProfileService userProfileService) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.userProfileService = userProfileService;
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

        // Log de actividad en MongoDB (best-effort: no bloquea el login si Mongo no responde).
        userProfileService.recordActivity(user.getId(), UserProfileService.ACTIVITY_LOGIN,
                "Inicio de sesión correcto");

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
