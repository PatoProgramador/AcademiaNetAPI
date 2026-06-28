package com.academianet.demo.common;

public final class RoleCodes {

    public static final String ADMINISTRATOR = "ADMINISTRATOR";
    public static final String PROFESSOR = "PROFESSOR";
    public static final String STUDENT = "STUDENT";

    private RoleCodes() {}

    public static String toFront(String code) {
        if (code == null) return null;
        return switch (code) {
            case ADMINISTRATOR -> "admin";
            case PROFESSOR -> "profesor";
            case STUDENT -> "estudiante";
            default -> code.toLowerCase();
        };
    }

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
