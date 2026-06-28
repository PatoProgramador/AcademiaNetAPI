package com.academianet.demo.repository;

import com.academianet.demo.entity.AcademicRecord;
import com.academianet.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AcademicRecordRepository extends JpaRepository<AcademicRecord, UUID> {
    List<AcademicRecord> findByStudent(User student);
}
