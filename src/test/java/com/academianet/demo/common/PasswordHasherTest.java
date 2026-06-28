package com.academianet.demo.common;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PasswordHasherTest {

    @Test
    void hash_isDeterministic() {
        assertThat(PasswordHasher.hash("123456")).isEqualTo(PasswordHasher.hash("123456"));
    }

    @Test
    void hash_producesSha256HexLength() {
        assertThat(PasswordHasher.hash("anything")).hasSize(64).matches("[0-9a-f]+");
    }

    @Test
    void hash_differsForDifferentInput() {
        assertThat(PasswordHasher.hash("a")).isNotEqualTo(PasswordHasher.hash("b"));
    }

    @Test
    void matches_trueForCorrectPassword() {
        String hash = PasswordHasher.hash("secret");
        assertThat(PasswordHasher.matches("secret", hash)).isTrue();
    }

    @Test
    void matches_falseForWrongPassword() {
        String hash = PasswordHasher.hash("secret");
        assertThat(PasswordHasher.matches("nope", hash)).isFalse();
    }

    @Test
    void matches_falseWhenNulls() {
        assertThat(PasswordHasher.matches(null, PasswordHasher.hash("x"))).isFalse();
        assertThat(PasswordHasher.matches("x", null)).isFalse();
    }
}
