package com.academiaNetAPI.demo.repository;

import com.academiaNetAPI.demo.entity.Company;
import com.academiaNetAPI.demo.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RoleRepository extends JpaRepository<Role, UUID> {
    Optional<Role> findByCompanyAndCode(Company company, String code);
}
