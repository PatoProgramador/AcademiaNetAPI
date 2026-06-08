package com.academiaNetAPI.demo.entity;

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

@Entity
@Table(name = "subjects", uniqueConstraints = {
        @UniqueConstraint(name = "uk_subjects_company_code", columnNames = {"company_id", "code"})
})
@SQLDelete(sql = "UPDATE subjects SET deleted_at = now() WHERE id = ?")
@SQLRestriction("deleted_at IS NULL")
@Getter
@Setter
@NoArgsConstructor
public class Subject extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "program_id")
    private Program program;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(nullable = false, length = 40)
    private String code;

    @Column(columnDefinition = "text")
    private String description;

    @Column(nullable = false)
    private Integer credits = 3;

    @Column(name = "weekly_hours")
    private Integer weeklyHours;

    @Column(length = 40)
    private String level;

    @Column(nullable = false)
    private boolean mandatory = true;

    @Column(nullable = false)
    private boolean active = true;
}
