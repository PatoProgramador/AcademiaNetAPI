package com.academianet.demo.repository;

import com.academianet.demo.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CompanyRepository extends JpaRepository<Company, UUID> {
    Optional<Company> findByDomain(String domain);
    Optional<Company> findByNit(String nit);
}
