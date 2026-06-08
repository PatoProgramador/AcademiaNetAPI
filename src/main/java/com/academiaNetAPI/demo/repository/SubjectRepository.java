package com.academiaNetAPI.demo.repository;

import com.academiaNetAPI.demo.entity.Company;
import com.academiaNetAPI.demo.entity.Subject;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SubjectRepository extends JpaRepository<Subject, UUID> {
    List<Subject> findByCompany(Company company);
    Optional<Subject> findByCompanyAndCode(Company company, String code);
}
