package com.academianet.demo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(name = "grades", uniqueConstraints = {
        @UniqueConstraint(name = "uk_grade_enrollment_evaluation",
                columnNames = {"company_id", "enrollment_id", "evaluation_id"})
})
@SQLDelete(sql = "UPDATE grades SET deleted_at = now() WHERE id = ?")
@SQLRestriction("deleted_at IS NULL")
@Getter
@Setter
@NoArgsConstructor
public class Grade extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "enrollment_id", nullable = false)
    private Enrollment enrollment;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "evaluation_id", nullable = false)
    private Evaluation evaluation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "professor_id")
    private User professor;

    @Column(name = "grade_value", nullable = false, precision = 5, scale = 2)
    private BigDecimal value;

    @Column(name = "max_value", nullable = false, precision = 5, scale = 2)
    private BigDecimal maxValue = new BigDecimal("5.00");

    @Column(columnDefinition = "text")
    private String observation;

    @Column(nullable = false)
    private boolean published = false;

    @Column(name = "record_date")
    private OffsetDateTime recordDate;
}
