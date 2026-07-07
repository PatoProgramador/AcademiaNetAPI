package com.academianet.demo.service;

import com.academianet.demo.document.UserActivity;
import com.academianet.demo.document.UserPreferences;
import com.academianet.demo.dto.UserActivityResponse;
import com.academianet.demo.dto.UserPreferencesRequest;
import com.academianet.demo.dto.UserPreferencesResponse;
import com.academianet.demo.exception.NotFoundException;
import com.academianet.demo.repository.UserRepository;
import com.academianet.demo.repository.mongo.UserActivityRepository;
import com.academianet.demo.repository.mongo.UserPreferencesRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Gestión de los datos auxiliares de usuario guardados en MongoDB: preferencias y log de
 * actividad. La identidad del usuario vive en PostgreSQL ({@link UserRepository}); aquí solo
 * validamos su existencia y asociamos documentos Mongo por {@code userId}.
 */
@Service
public class UserProfileService {

    public static final String ACTIVITY_LOGIN = "LOGIN";
    public static final String ACTIVITY_PREFERENCES_UPDATE = "PREFERENCES_UPDATE";

    private static final Logger log = LoggerFactory.getLogger(UserProfileService.class);
    private static final int MAX_ACTIVITY = 50;

    // Valores por defecto cuando el usuario aún no ha guardado preferencias.
    private static final String DEFAULT_THEME = "system";
    private static final String DEFAULT_LANGUAGE = "es";
    private static final String DEFAULT_TIMEZONE = "America/Bogota";
    private static final boolean DEFAULT_EMAIL_NOTIFICATIONS = true;

    private final UserPreferencesRepository preferencesRepository;
    private final UserActivityRepository activityRepository;
    private final UserRepository userRepository;

    public UserProfileService(UserPreferencesRepository preferencesRepository,
                              UserActivityRepository activityRepository,
                              UserRepository userRepository) {
        this.preferencesRepository = preferencesRepository;
        this.activityRepository = activityRepository;
        this.userRepository = userRepository;
    }

    public UserPreferencesResponse getPreferences(UUID userId) {
        ensureUserExists(userId);
        UserPreferences prefs = preferencesRepository.findById(userId.toString())
                .orElseGet(() -> defaults(userId));
        return toResponse(prefs);
    }

    public UserPreferencesResponse updatePreferences(UUID userId, UserPreferencesRequest request) {
        ensureUserExists(userId);
        UserPreferences prefs = preferencesRepository.findById(userId.toString())
                .orElseGet(() -> defaults(userId));
        prefs.setTheme(request.theme());
        prefs.setLanguage(request.language());
        prefs.setEmailNotifications(request.emailNotifications());
        prefs.setTimezone(request.timezone());
        prefs.setUpdatedAt(Instant.now());
        UserPreferences saved = preferencesRepository.save(prefs);
        recordActivity(userId, ACTIVITY_PREFERENCES_UPDATE, "Preferencias actualizadas");
        return toResponse(saved);
    }

    public List<UserActivityResponse> listActivity(UUID userId) {
        ensureUserExists(userId);
        return activityRepository
                .findByUserIdOrderByTimestampDesc(userId.toString(), PageRequest.of(0, MAX_ACTIVITY))
                .stream()
                .map(UserProfileService::toResponse)
                .toList();
    }

    /**
     * Registra un evento de actividad. Es best-effort: si MongoDB no está disponible se ignora
     * (solo se deja un warning) para no romper la operación de negocio que lo dispara (p.ej. login).
     */
    public void recordActivity(UUID userId, String type, String detail) {
        try {
            UserActivity activity = new UserActivity();
            activity.setUserId(userId.toString());
            activity.setType(type);
            activity.setDetail(detail);
            activity.setTimestamp(Instant.now());
            activityRepository.save(activity);
        } catch (RuntimeException ex) {
            log.warn("No se pudo registrar la actividad '{}' del usuario {} en MongoDB: {}",
                    type, userId, ex.getMessage());
        }
    }

    private void ensureUserExists(UUID userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Usuario no encontrado: " + userId);
        }
    }

    private UserPreferences defaults(UUID userId) {
        UserPreferences prefs = new UserPreferences();
        prefs.setUserId(userId.toString());
        prefs.setTheme(DEFAULT_THEME);
        prefs.setLanguage(DEFAULT_LANGUAGE);
        prefs.setEmailNotifications(DEFAULT_EMAIL_NOTIFICATIONS);
        prefs.setTimezone(DEFAULT_TIMEZONE);
        return prefs;
    }

    private static UserPreferencesResponse toResponse(UserPreferences prefs) {
        return new UserPreferencesResponse(
                UUID.fromString(prefs.getUserId()),
                prefs.getTheme(),
                prefs.getLanguage(),
                prefs.isEmailNotifications(),
                prefs.getTimezone(),
                prefs.getUpdatedAt()
        );
    }

    private static UserActivityResponse toResponse(UserActivity activity) {
        return new UserActivityResponse(
                activity.getId(),
                activity.getType(),
                activity.getDetail(),
                activity.getTimestamp()
        );
    }
}
