package com.academiaNetAPI.demo.service;

import com.academiaNetAPI.demo.entity.Company;
import com.academiaNetAPI.demo.exception.NotFoundException;
import com.academiaNetAPI.demo.repository.CompanyRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class TenantService {

    private final CompanyRepository companyRepository;

    public TenantService(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    public Company resolve(UUID companyId) {
        if (companyId != null) {
            return companyRepository.findById(companyId)
                    .orElseThrow(() -> new NotFoundException("Empresa no encontrada: " + companyId));
        }
        return companyRepository.findAll().stream()
                .findFirst()
                .orElseThrow(() -> new NotFoundException("No hay ninguna empresa registrada"));
    }
}
