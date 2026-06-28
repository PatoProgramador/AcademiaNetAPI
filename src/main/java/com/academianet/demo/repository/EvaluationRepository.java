package com.academianet.demo.repository;

import com.academianet.demo.entity.Course;
import com.academianet.demo.entity.Evaluation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface EvaluationRepository extends JpaRepository<Evaluation, UUID> {
    List<Evaluation> findByCourse(Course course);
}
