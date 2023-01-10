package org.komponente.rentalservice.mapper;

import org.komponente.dto.carrental.CompanyCarCreateDto;
import org.komponente.dto.carrental.CompanyCarDto;
import org.komponente.rentalservice.domain.CompanyCar;
import org.komponente.rentalservice.exceptions.NotFoundException;
import org.komponente.rentalservice.repository.CompanyRepository;
import org.komponente.rentalservice.repository.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CompanyCarMapper {

    private static CompanyRepository companyRepository;
    private static VehicleRepository vehicleRepository;

    @Autowired
    public void InitRepo(CompanyRepository companyRepository, VehicleRepository vehicleRepository){
        CompanyCarMapper.companyRepository = companyRepository;
        CompanyCarMapper.vehicleRepository = vehicleRepository;
    }

    public static CompanyCarDto companyCarToCompanyCarDto(CompanyCar companyCar){
        CompanyCarDto companyCarDto = new CompanyCarDto();
        companyCarDto.setCompanyDto(CompanyMapper.companyToCompanyDto(companyCar.getCompany()));
        companyCarDto.setVehicleDto(VehicleMapper.vehicleToVehicleDto(companyCar.getVehicle()));
        companyCarDto.setId(companyCar.getId());
        companyCarDto.setPrice(companyCar.getPrice());
        companyCarDto.setNumberofcars(companyCar.getNumberofcars());
        return companyCarDto;
    }

    public static CompanyCar companyCarCreateDtoToCompanyCar(CompanyCarCreateDto companyCarCreateDto)
    {
        CompanyCar companyCar = new CompanyCar();
        companyCar.setCompany(companyRepository.findById(companyCarCreateDto.getCompanyid()).orElseThrow(() -> new NotFoundException(String
                .format("Company with id: %d does not exists.", companyCarCreateDto.getCompanyid()))));
        companyCar.setVehicle(vehicleRepository.findById(companyCarCreateDto.getVehicleid()).orElseThrow(() -> new NotFoundException(String
                .format("Vehicle with id: %d does not exists.", companyCarCreateDto.getVehicleid()))));
        companyCar.setPrice(companyCarCreateDto.getPrice());
        companyCar.setNumberofcars(companyCarCreateDto.getNumberofcars());
        return companyCar;
    }
}
