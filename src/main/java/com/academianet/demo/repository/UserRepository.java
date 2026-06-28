package com.academianet.demo.repository;

import com.academianet.demo.entity.Company;
import com.academianet.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findFirstByEmailIgnoreCase(String email);

    Optional<User> findByCompanyAndEmailIgnoreCase(Company company, String email);

    boolean existsByCompanyAndEmailIgnoreCase(Company company, String email);

    List<User> findByCompanyOrderByFirstNameAsc(Company company);

    List<User> findByCompanyAndRole_CodeOrderByFirstNameAsc(Company company, String roleCode);
}
