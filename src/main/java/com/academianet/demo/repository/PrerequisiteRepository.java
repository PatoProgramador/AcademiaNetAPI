package com.academianet.demo.repository;

import com.academianet.demo.entity.Prerequisite;
import com.academianet.demo.entity.Subject;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PrerequisiteRepository extends JpaRepository<Prerequisite, UUID> {
    List<Prerequisite> findBySubject(Subject subject);
}
