package com.academiaNetAPI.demo.entity;

import com.academiaNetAPI.demo.enums.EvaluationType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * EVALUACIONES — estructura de calificación de un CURSO (parcial 30%, final 40%...).
 * La suma de los porcentajes de un curso debe ser 100.
 */
@Entity
@Table(name = "evaluations")
@SQLDelete(sql = "UPDATE evaluations SET deleted_at = now() WHERE id = ?")
@SQLRestriction("deleted_at IS NULL")
@Getter
@Setter
@NoArgsConstructor
public class Evaluation extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @Column(nullable = false, length = 150)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private EvaluationType type;

    @Column(precision = 5, scale = 2)
    private BigDecimal percentage;

    private LocalDate date;

    @Column(columnDefinition = "text")
    private String description;
}
