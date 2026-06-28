package com.academianet.demo.repository;

import com.academianet.demo.entity.Company;
import com.academianet.demo.entity.Permission;
import com.academianet.demo.enums.PermissionAction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PermissionRepository extends JpaRepository<Permission, UUID> {
    Optional<Permission> findByCompanyAndResourceAndAction(Company company, String resource, PermissionAction action);
}
