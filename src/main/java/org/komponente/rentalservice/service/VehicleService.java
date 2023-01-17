package org.komponente.rentalservice.service;

import org.komponente.dto.carrental.CompanyCarChangeDto;
import org.komponente.dto.carrental.CompanyCarCreateDto;
import org.komponente.dto.carrental.CompanyCarDto;
import org.komponente.dto.vehicle.VehicleCreateDto;
import org.komponente.dto.vehicle.VehicleDto;
import org.komponente.dto.vehicle.VehicleTypeCreateDto;
import org.komponente.dto.vehicle.VehicleTypeDto;
import org.komponente.rentalservice.domain.VehicleType;

public interface VehicleService {

    VehicleTypeDto createVehicleType(VehicleTypeCreateDto vehicleTypeCreateDto);

    VehicleDto createVehicle(VehicleCreateDto vehicleCreateDto);

    CompanyCarDto createCompanyCar(CompanyCarCreateDto companyCarCreateDto);

    CompanyCarDto changePrice(CompanyCarChangeDto companyCarChangeDto);
}
