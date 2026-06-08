package com.academiaNetAPI.demo.repository;

import com.academiaNetAPI.demo.entity.AcademicRecord;
import com.academiaNetAPI.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AcademicRecordRepository extends JpaRepository<AcademicRecord, UUID> {
    List<AcademicRecord> findByStudent(User student);
}
