package com.academianet.demo.repository;

import com.academianet.demo.entity.Classroom;
import com.academianet.demo.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ClassroomRepository extends JpaRepository<Classroom, UUID> {
    List<Classroom> findByCompany(Company company);
}
