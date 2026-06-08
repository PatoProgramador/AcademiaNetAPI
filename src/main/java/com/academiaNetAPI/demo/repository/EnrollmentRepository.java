package com.academiaNetAPI.demo.repository;

import com.academiaNetAPI.demo.entity.Course;
import com.academiaNetAPI.demo.entity.Enrollment;
import com.academiaNetAPI.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface EnrollmentRepository extends JpaRepository<Enrollment, UUID> {
    List<Enrollment> findByStudent(User student);
    List<Enrollment> findByCourse(Course course);
    List<Enrollment> findByCourse_Professor(User professor);
}
