package com.academianet.demo.document;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

/**
 * Preferencias de usuario almacenadas en MongoDB. El {@code id} es el UUID (como String)
 * del usuario que vive en PostgreSQL: no duplicamos identidad, solo datos auxiliares.
 */
@Document(collection = "user_preferences")
@Getter
@Setter
@NoArgsConstructor
public class UserPreferences {

    @Id
    private String userId;

    /** "light" | "dark" | "system" */
    private String theme;

    /** ISO 639-1, p.ej. "es" | "en" */
    private String language;

    private boolean emailNotifications;

    private String timezone;

    private Instant updatedAt;
}
