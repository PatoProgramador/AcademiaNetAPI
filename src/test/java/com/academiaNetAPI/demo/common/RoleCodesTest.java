package com.academiaNetAPI.demo.common;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RoleCodesTest {

    @Test
    void toFront_mapsKnownCodes() {
        assertThat(RoleCodes.toFront("ADMINISTRATOR")).isEqualTo("admin");
        assertThat(RoleCodes.toFront("PROFESSOR")).isEqualTo("profesor");
        assertThat(RoleCodes.toFront("STUDENT")).isEqualTo("estudiante");
    }

    @Test
    void toFront_handlesNullAndUnknown() {
        assertThat(RoleCodes.toFront(null)).isNull();
        assertThat(RoleCodes.toFront("CUSTOM")).isEqualTo("custom");
    }

    @ParameterizedTest
    @CsvSource({
            "admin,ADMINISTRATOR",
            "administrador,ADMINISTRATOR",
            "administrator,ADMINISTRATOR",
            "profesor,PROFESSOR",
            "professor,PROFESSOR",
            "teacher,PROFESSOR",
            "estudiante,STUDENT",
            "student,STUDENT",
            "alumno,STUDENT",
            "  ADMIN  ,ADMINISTRATOR",
            "Profesor,PROFESSOR"
    })
    void fromFront_acceptsSynonymsAndIsCaseInsensitive(String input, String expected) {
        assertThat(RoleCodes.fromFront(input)).isEqualTo(expected);
    }

    @Test
    void fromFront_nullReturnsNull() {
        assertThat(RoleCodes.fromFront(null)).isNull();
    }

    @Test
    void fromFront_unknownThrows() {
        assertThatThrownBy(() -> RoleCodes.fromFront("ninja"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("ninja");
    }

    @Test
    void roundTrip_isConsistent() {
        for (String code : new String[]{RoleCodes.ADMINISTRATOR, RoleCodes.PROFESSOR, RoleCodes.STUDENT}) {
            assertThat(RoleCodes.fromFront(RoleCodes.toFront(code))).isEqualTo(code);
        }
    }
}
