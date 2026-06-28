package com.academianet.demo.service;

import com.academianet.demo.entity.Company;
import com.academianet.demo.exception.NotFoundException;
import com.academianet.demo.repository.CompanyRepository;
import com.academianet.demo.support.TestEntities;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TenantServiceTest {

    @Mock CompanyRepository companyRepository;
    @InjectMocks TenantService tenantService;

    @Test
    void resolve_byIdReturnsCompany() {
        Company company = TestEntities.company("Acme");
        when(companyRepository.findById(company.getId())).thenReturn(Optional.of(company));

        assertThat(tenantService.resolve(company.getId())).isSameAs(company);
    }

    @Test
    void resolve_byMissingIdThrows() {
        UUID id = UUID.randomUUID();
        when(companyRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> tenantService.resolve(id)).isInstanceOf(NotFoundException.class);
    }

    @Test
    void resolve_nullIdReturnsFirstCompany() {
        Company company = TestEntities.company("Default");
        when(companyRepository.findAll()).thenReturn(List.of(company));

        assertThat(tenantService.resolve(null)).isSameAs(company);
    }

    @Test
    void resolve_nullIdWithNoCompaniesThrows() {
        when(companyRepository.findAll()).thenReturn(List.of());

        assertThatThrownBy(() -> tenantService.resolve(null)).isInstanceOf(NotFoundException.class);
    }
}
