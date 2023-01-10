package org.komponente.rentalservice.mapper;

import org.komponente.dto.company.CompanyCreateDto;
import org.komponente.dto.company.CompanyDto;
import org.komponente.rentalservice.domain.Company;

public class CompanyMapper {
    public static CompanyDto companyToCompanyDto(Company company){
        CompanyDto companyDto = new CompanyDto();
        companyDto.setName(company.getName());
        companyDto.setDescription(company.getDescription());
        return companyDto;
    }

    public static Company companyCreateDtoToCompany(CompanyCreateDto companyCreateDto){
        Company company = new Company();
        company.setDescription(companyCreateDto.getDescription());
        company.setName(companyCreateDto.getName());
        return company;
    }
}
