package com.academiaNetAPI.demo.entity;

import com.academiaNetAPI.demo.enums.ProgramLevel;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

/**
 * PROGRAMAS — agrupa MATERIAS en planes de estudio / carreras.
 */
@Entity
@Table(name = "programs", uniqueConstraints = {
        @UniqueConstraint(name = "uk_programs_company_code", columnNames = {"company_id", "code"})
})
@SQLDelete(sql = "UPDATE programs SET deleted_at = now() WHERE id = ?")
@SQLRestriction("deleted_at IS NULL")
@Getter
@Setter
@NoArgsConstructor
public class Program extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(nullable = false, length = 40)
    private String code;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private ProgramLevel level;

    @Column(name = "duration_semesters")
    private Integer durationSemesters;

    @Column(nullable = false)
    private boolean active = true;
}
