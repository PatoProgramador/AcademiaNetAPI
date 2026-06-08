package com.academiaNetAPI.demo.common;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

public final class PasswordHasher {

    private PasswordHasher() {}

    public static String hash(String raw) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] bytes = digest.digest(raw.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(bytes);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 no disponible", e);
        }
    }

    public static boolean matches(String raw, String hash) {
        if (raw == null || hash == null) return false;
        return MessageDigest.isEqual(
                hash.getBytes(StandardCharsets.UTF_8),
                hash(raw).getBytes(StandardCharsets.UTF_8));
    }
}
