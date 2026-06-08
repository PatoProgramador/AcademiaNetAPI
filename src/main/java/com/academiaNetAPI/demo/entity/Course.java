package com.academiaNetAPI.demo.entity;

import com.academiaNetAPI.demo.enums.CourseModality;
import com.academiaNetAPI.demo.enums.CourseStatus;
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

@Entity
@Table(name = "courses")
@SQLDelete(sql = "UPDATE courses SET deleted_at = now() WHERE id = ?")
@SQLRestriction("deleted_at IS NULL")
@Getter
@Setter
@NoArgsConstructor
public class Course extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "professor_id")
    private User professor;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "period_id", nullable = false)
    private AcademicPeriod period;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "classroom_id")
    private Classroom classroom;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(length = 40)
    private String code;

    @Column(name = "max_capacity")
    private Integer maxCapacity;

    @Column(name = "available_capacity")
    private Integer availableCapacity;

    @Column(length = 100)
    private String schedule;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private CourseModality modality = CourseModality.IN_PERSON;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private CourseStatus status = CourseStatus.OPEN;
}
