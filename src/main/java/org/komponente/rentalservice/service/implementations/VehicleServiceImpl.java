package org.komponente.rentalservice.service.implementations;

import lombok.AllArgsConstructor;
import org.komponente.dto.carrental.CompanyCarChangeDto;
import org.komponente.dto.carrental.CompanyCarCreateDto;
import org.komponente.dto.carrental.CompanyCarDto;
import org.komponente.dto.vehicle.VehicleCreateDto;
import org.komponente.dto.vehicle.VehicleDto;
import org.komponente.dto.vehicle.VehicleTypeCreateDto;
import org.komponente.dto.vehicle.VehicleTypeDto;
import org.komponente.rentalservice.domain.Company;
import org.komponente.rentalservice.domain.CompanyCar;
import org.komponente.rentalservice.domain.Vehicle;
import org.komponente.rentalservice.domain.VehicleType;
import org.komponente.rentalservice.exceptions.AlreadyExistsException;
import org.komponente.rentalservice.exceptions.NotFoundException;
import org.komponente.rentalservice.mapper.CompanyCarMapper;
import org.komponente.rentalservice.mapper.VehicleMapper;
import org.komponente.rentalservice.mapper.VehicleTypeMapper;
import org.komponente.rentalservice.repository.CompanyCarRepository;
import org.komponente.rentalservice.repository.CompanyRepository;
import org.komponente.rentalservice.repository.VehicleRepository;
import org.komponente.rentalservice.repository.VehicleTypeRepository;
import org.komponente.rentalservice.service.VehicleService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class VehicleServiceImpl implements VehicleService {

    private VehicleRepository vehicleRepository;

    private VehicleTypeRepository vehicleTypeRepository;

    private CompanyCarRepository companyCarRepository;
    private CompanyRepository companyRepository;


    @Override
    public VehicleTypeDto createVehicleType(VehicleTypeCreateDto vehicleTypeCreateDto) {
        if(vehicleTypeRepository.findVehicleTypeByType(vehicleTypeCreateDto.getType()).isPresent()) {
            throw new AlreadyExistsException("Vehicle type already exists");
        }
        VehicleType vehicleType = VehicleTypeMapper.vehicleTypeCreateDtoToVehicleType(vehicleTypeCreateDto);
        vehicleTypeRepository.save(vehicleType);
        return VehicleTypeMapper.vehicleToVehicleDto(vehicleType);
    }

    @Override
    public VehicleDto createVehicle(VehicleCreateDto vehicleCreateDto) {
        Vehicle vehicle = VehicleMapper.vehicleCreateDtoToVehicle(vehicleCreateDto);
        vehicleRepository.save(vehicle);
        return VehicleMapper.vehicleToVehicleDto(vehicle);
    }

    @Override
    public CompanyCarDto createCompanyCar(CompanyCarCreateDto companyCarCreateDto) {
        Vehicle vehicle = vehicleRepository.findById(companyCarCreateDto.getVehicleid()).orElseThrow(() -> new NotFoundException("Vehicle not found"));
        Company company = companyRepository.findById(companyCarCreateDto.getCompanyid()).orElseThrow(() -> new NotFoundException("Company not found"));
        if(companyCarRepository.findCompanyCarByCompanyAndVehicle(company, vehicle).isPresent()) {
            throw new AlreadyExistsException("This vehicle is already assigned to the company!");
        }
        CompanyCar companyCar = CompanyCarMapper.companyCarCreateDtoToCompanyCar(companyCarCreateDto);
        companyCarRepository.save(companyCar);
        return CompanyCarMapper.companyCarToCompanyCarDto(companyCar);
    }

    @Override
    public CompanyCarDto changePrice(CompanyCarChangeDto companyCarChangeDto) {
        CompanyCar companyCar = companyCarRepository.findById(companyCarChangeDto.getId()).orElseThrow(() ->
                new NotFoundException("Company car with id " + companyCarChangeDto.getId() + " does not exist!"));
        companyCar.setPrice(companyCarChangeDto.getPrice());
        companyCarRepository.save(companyCar);
        return CompanyCarMapper.companyCarToCompanyCarDto(companyCar);
    }

    @Override
    public List<VehicleTypeDto> getAllVehicleTypes() {
        return vehicleTypeRepository.findAll().stream().map(VehicleTypeMapper::vehicleToVehicleDto).collect(Collectors.toList());
    }

    @Override
    public List<VehicleDto> getAllVehicles() {
        return vehicleRepository.findAll().stream().map(VehicleMapper::vehicleToVehicleDto).collect(Collectors.toList());
    }


}
