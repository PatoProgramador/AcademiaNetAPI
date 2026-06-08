package com.academiaNetAPI.demo.repository;

import com.academiaNetAPI.demo.entity.AcademicPeriod;
import com.academiaNetAPI.demo.entity.Company;
import com.academiaNetAPI.demo.enums.PeriodStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AcademicPeriodRepository extends JpaRepository<AcademicPeriod, UUID> {
    List<AcademicPeriod> findByCompany(Company company);
    Optional<AcademicPeriod> findFirstByCompanyAndStatus(Company company, PeriodStatus status);
    Optional<AcademicPeriod> findByCompanyAndCode(Company company, String code);
}
