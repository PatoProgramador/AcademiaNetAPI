package com.academianet.demo.controller;

import com.academianet.demo.dto.UserActivityResponse;
import com.academianet.demo.dto.UserPreferencesRequest;
import com.academianet.demo.dto.UserPreferencesResponse;
import com.academianet.demo.service.UserProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users/{id}")
@Tag(name = "Perfil de usuario (MongoDB)",
        description = "Datos auxiliares de usuario almacenados en MongoDB: preferencias y actividad")
public class UserProfileController {

    private final UserProfileService userProfileService;

    public UserProfileController(UserProfileService userProfileService) {
        this.userProfileService = userProfileService;
    }

    @GetMapping("/preferences")
    @Operation(summary = "Obtener preferencias del usuario",
            description = "Devuelve las preferencias guardadas en MongoDB o los valores por defecto.")
    public UserPreferencesResponse getPreferences(@PathVariable UUID id) {
        return userProfileService.getPreferences(id);
    }

    @PutMapping("/preferences")
    @Operation(summary = "Actualizar preferencias del usuario")
    public UserPreferencesResponse updatePreferences(@PathVariable UUID id,
                                                     @Valid @RequestBody UserPreferencesRequest request) {
        return userProfileService.updatePreferences(id, request);
    }

    @GetMapping("/activity")
    @Operation(summary = "Historial de actividad del usuario",
            description = "Últimos eventos (login, cambios de preferencias) registrados en MongoDB.")
    public List<UserActivityResponse> listActivity(@PathVariable UUID id) {
        return userProfileService.listActivity(id);
    }
}
