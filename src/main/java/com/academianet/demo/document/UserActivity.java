package com.academianet.demo.document;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

/**
 * Evento de actividad de un usuario (login, cambio de preferencias, ...) almacenado en MongoDB.
 * Se relaciona con el usuario de PostgreSQL por {@code userId} (UUID como String).
 */
@Document(collection = "user_activity")
@Getter
@Setter
@NoArgsConstructor
public class UserActivity {

    @Id
    private String id;

    @Indexed
    private String userId;

    /** Tipo de evento, p.ej. "LOGIN" | "PREFERENCES_UPDATE". */
    private String type;

    private String detail;

    private Instant timestamp;
}
