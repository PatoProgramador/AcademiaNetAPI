package com.academiaNetAPI.demo.entity;

import com.academiaNetAPI.demo.enums.ClassroomType;
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

/**
 * AULAS — espacios físicos o virtuales asignados a un curso (control de capacidad).
 */
@Entity
@Table(name = "classrooms")
@SQLDelete(sql = "UPDATE classrooms SET deleted_at = now() WHERE id = ?")
@SQLRestriction("deleted_at IS NULL")
@Getter
@Setter
@NoArgsConstructor
public class Classroom extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 40)
    private String code;

    @Column(length = 100)
    private String building;

    private Integer capacity;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private ClassroomType type;
}
