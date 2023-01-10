package org.komponente.rentalservice.mapper;

import org.komponente.dto.vehicle.VehicleCreateDto;
import org.komponente.dto.vehicle.VehicleDto;
import org.komponente.rentalservice.domain.Vehicle;
import org.komponente.rentalservice.exceptions.NotFoundException;
import org.komponente.rentalservice.repository.VehicleTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class VehicleMapper {

    private static VehicleTypeRepository vehicleTypeRepository;

    @Autowired
    public void InitRep(VehicleTypeRepository vehicleTypeRepository){
        VehicleMapper.vehicleTypeRepository = vehicleTypeRepository;
    }

    public static VehicleDto vehicleToVehicleDto(Vehicle vehicle)
    {
        VehicleDto vehicleDto = new VehicleDto();
        vehicleDto.setId(vehicle.getId());
        vehicleDto.setName(vehicle.getName());
        vehicleDto.setVehicleTypeDto(VehicleTypeMapper.vehicleToVehicleDto(vehicle.getVehicleType()));
        return  vehicleDto;
    }

    public static Vehicle vehicleCreateDtoToVehicle(VehicleCreateDto vehicleCreateDto)
    {
        Vehicle vehicle = new Vehicle();
        vehicle.setVehicleType(vehicleTypeRepository.findById(vehicleCreateDto.getVehicleTypeId()).orElseThrow(() -> new NotFoundException(String
                .format("Vehicle type with id: %d does not exists.", vehicleCreateDto.getVehicleTypeId()))));
        vehicle.setName(vehicleCreateDto.getName());
        return vehicle;
    }
}
