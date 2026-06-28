package com.academianet.demo.security;

import com.academianet.demo.entity.Company;
import com.academianet.demo.entity.Role;
import com.academianet.demo.entity.User;
import com.academianet.demo.support.TestEntities;
import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtServiceTest {

    private final JwtService jwtService = new JwtService("una-clave-de-prueba-cualquiera", 3_600_000L);

    private User sampleUser() {
        Company company = TestEntities.company("Demo");
        Role role = TestEntities.role(company, "STUDENT");
        return TestEntities.user(company, role, "Ana", "García", "ana@test.com");
    }

    @Test
    void generateAndParse_roundTrip() {
        User user = sampleUser();
        String token = jwtService.generateToken(user);

        JwtPrincipal principal = jwtService.parse(token);

        assertThat(principal.userId()).isEqualTo(user.getId());
        assertThat(principal.email()).isEqualTo("ana@test.com");
        assertThat(principal.role()).isEqualTo("STUDENT");
        assertThat(principal.companyId()).isEqualTo(user.getCompany().getId());
        assertThat(principal.name()).isEqualTo("Ana García");
    }

    @Test
    void parse_malformedTokenThrows() {
        assertThatThrownBy(() -> jwtService.parse("a.b.c")).isInstanceOf(JwtException.class);
    }

    @Test
    void parse_tamperedTokenThrows() {
        String token = jwtService.generateToken(sampleUser());
        String tampered = token.substring(0, token.length() - 2) + "xx";
        assertThatThrownBy(() -> jwtService.parse(tampered)).isInstanceOf(JwtException.class);
    }

    @Test
    void parse_expiredTokenThrows() {
        JwtService shortLived = new JwtService("una-clave-de-prueba-cualquiera", -1_000L);
        String expired = shortLived.generateToken(sampleUser());
        assertThatThrownBy(() -> shortLived.parse(expired)).isInstanceOf(JwtException.class);
    }

    @Test
    void differentSecret_cannotVerify() {
        String token = jwtService.generateToken(sampleUser());
        JwtService other = new JwtService("otra-clave-totalmente-distinta", 3_600_000L);
        assertThatThrownBy(() -> other.parse(token)).isInstanceOf(JwtException.class);
    }

    @Test
    void exposesConfiguredExpiration() {
        assertThat(jwtService.getExpirationMs()).isEqualTo(3_600_000L);
    }
}
