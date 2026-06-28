package com.academianet.demo.repository;

import com.academianet.demo.entity.AcademicPeriod;
import com.academianet.demo.entity.Company;
import com.academianet.demo.enums.PeriodStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AcademicPeriodRepository extends JpaRepository<AcademicPeriod, UUID> {
    List<AcademicPeriod> findByCompany(Company company);
    Optional<AcademicPeriod> findFirstByCompanyAndStatus(Company company, PeriodStatus status);
    Optional<AcademicPeriod> findByCompanyAndCode(Company company, String code);
}
