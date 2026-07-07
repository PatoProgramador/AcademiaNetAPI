package com.academianet.demo.support;

import com.mongodb.MongoClientSettings;
import org.springframework.boot.mongodb.autoconfigure.MongoClientSettingsBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import java.util.concurrent.TimeUnit;

/**
 * En los tests no hay un MongoDB real. Este customizer fuerza timeouts muy cortos para que
 * el registro de actividad (best-effort) falle en milisegundos en lugar de bloquear 30 s con
 * el timeout de selección de servidor por defecto del driver. Se aplica después del customizer
 * de Spring Boot (orden LOWEST_PRECEDENCE) para sobreescribir la configuración de la URI.
 *
 * <p>Al estar en el paquete base de la app, se incluye automáticamente en todos los
 * {@code @SpringBootTest} vía component-scan.
 */
@Configuration
public class TestMongoConfig {

    @Bean
    @Order(Ordered.LOWEST_PRECEDENCE)
    MongoClientSettingsBuilderCustomizer fastFailMongoCustomizer() {
        return (MongoClientSettings.Builder builder) -> builder
                .applyToClusterSettings(s -> s.serverSelectionTimeout(200, TimeUnit.MILLISECONDS))
                .applyToSocketSettings(s -> s.connectTimeout(200, TimeUnit.MILLISECONDS));
    }
}
