package com.academianet.demo.repository;

import com.academianet.demo.entity.Enrollment;
import com.academianet.demo.entity.Grade;
import com.academianet.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface GradeRepository extends JpaRepository<Grade, UUID> {
    List<Grade> findByEnrollment(Enrollment enrollment);
    List<Grade> findByEnrollment_Student(User student);
    List<Grade> findByEnrollment_StudentAndPublishedTrue(User student);
}
