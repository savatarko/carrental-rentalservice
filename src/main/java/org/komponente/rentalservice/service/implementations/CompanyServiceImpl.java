package org.komponente.rentalservice.service.implementations;

import lombok.AllArgsConstructor;
import org.komponente.dto.company.ChangeCompanyDto;
import org.komponente.dto.company.CompanyCreateDto;
import org.komponente.dto.company.CompanyDto;
import org.komponente.rentalservice.domain.Company;
import org.komponente.rentalservice.exceptions.AlreadyExistsException;
import org.komponente.rentalservice.exceptions.CompanyAlreadyHasManagerException;
import org.komponente.rentalservice.exceptions.NotFoundException;
import org.komponente.rentalservice.mapper.CompanyMapper;
import org.komponente.rentalservice.repository.CompanyRepository;
import org.komponente.rentalservice.service.CompanyService;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class CompanyServiceImpl implements CompanyService {
    private CompanyRepository companyRepository;

    public CompanyDto createCompany(CompanyCreateDto companyCreateDto){
        if(companyRepository.findCompanyByName(companyCreateDto.getName()) != null){
            throw new AlreadyExistsException("Company with name " + companyCreateDto.getName() + " already exists");
        }
        Company company = CompanyMapper.companyCreateDtoToCompany(companyCreateDto);
        company.setManagerid((long) -1);
        companyRepository.save(company);
        return CompanyMapper.companyToCompanyDto(company);
    }

    @Override
    public CompanyDto registerManagerToCompany(Long id, Long companyid) {
        Company company = companyRepository.findById(companyid).orElseThrow(()->new NotFoundException("Company with id " + companyid +" not found!"));
        if(company.getManagerid()!=-1){
            throw new CompanyAlreadyHasManagerException("Company with id " + companyid + " already has a manager assigned.");
        }
        company.setManagerid(id);
        companyRepository.save(company);
        return CompanyMapper.companyToCompanyDto(company);
    }

    @Override
    public CompanyDto changeCompany(ChangeCompanyDto companyDto, Long managerid) {
        Company company = companyRepository.findCompanyByManagerid(managerid).orElseThrow(()->new NotFoundException("Company with managerid " + managerid +" not found!"));
        company.setName(companyDto.getName());
        company.setDescription(companyDto.getDescription());
        companyRepository.save(company);
        return CompanyMapper.companyToCompanyDto(company);
    }
}
