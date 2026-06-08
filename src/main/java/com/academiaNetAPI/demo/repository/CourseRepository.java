package com.academiaNetAPI.demo.repository;

import com.academiaNetAPI.demo.entity.Company;
import com.academiaNetAPI.demo.entity.Course;
import com.academiaNetAPI.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CourseRepository extends JpaRepository<Course, UUID> {
    List<Course> findByCompany(Company company);
    List<Course> findByProfessor(User professor);
}
