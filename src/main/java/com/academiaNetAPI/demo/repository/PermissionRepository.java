package com.academiaNetAPI.demo.repository;

import com.academiaNetAPI.demo.entity.Company;
import com.academiaNetAPI.demo.entity.Permission;
import com.academiaNetAPI.demo.enums.PermissionAction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PermissionRepository extends JpaRepository<Permission, UUID> {
    Optional<Permission> findByCompanyAndResourceAndAction(Company company, String resource, PermissionAction action);
}
