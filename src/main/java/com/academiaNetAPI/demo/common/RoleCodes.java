package com.academiaNetAPI.demo.common;

/**
 * Códigos de rol internos (en inglés) y su traducción a los valores que usa el
 * front (App.tsx): "estudiante" | "profesor" | "admin".
 */
public final class RoleCodes {

    public static final String ADMINISTRATOR = "ADMINISTRATOR";
    public static final String PROFESSOR = "PROFESSOR";
    public static final String STUDENT = "STUDENT";

    private RoleCodes() {}

    /** Código interno -> valor del front. */
    public static String toFront(String code) {
        if (code == null) return null;
        return switch (code) {
            case ADMINISTRATOR -> "admin";
            case PROFESSOR -> "profesor";
            case STUDENT -> "estudiante";
            default -> code.toLowerCase();
        };
    }

    /** Valor del front -> código interno. Acepta sinónimos comunes. */
    public static String fromFront(String role) {
        if (role == null) return null;
        return switch (role.trim().toLowerCase()) {
            case "admin", "administrador", "administrator" -> ADMINISTRATOR;
            case "profesor", "professor", "teacher" -> PROFESSOR;
            case "estudiante", "student", "alumno" -> STUDENT;
            default -> throw new IllegalArgumentException("Rol no reconocido: " + role);
        };
    }
}
