package com.academiaNetAPI.demo.repository;

import com.academiaNetAPI.demo.entity.Prerequisite;
import com.academiaNetAPI.demo.entity.Subject;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PrerequisiteRepository extends JpaRepository<Prerequisite, UUID> {
    List<Prerequisite> findBySubject(Subject subject);
}
