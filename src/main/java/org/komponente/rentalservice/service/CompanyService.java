package org.komponente.rentalservice.service;

import org.komponente.dto.company.CompanyCreateDto;
import org.komponente.dto.company.CompanyDto;

public interface CompanyService {

    CompanyDto createCompany(CompanyCreateDto companyCreateDto);

    CompanyDto registerManagerToCompany(Long id, Long companyid);
}
