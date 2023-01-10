package org.komponente.rentalservice.mapper;

import org.komponente.dto.vehicle.VehicleDto;
import org.komponente.dto.vehicle.VehicleTypeCreateDto;
import org.komponente.dto.vehicle.VehicleTypeDto;
import org.komponente.rentalservice.domain.VehicleType;

public class VehicleTypeMapper {
    public static VehicleTypeDto vehicleToVehicleDto(VehicleType vehicleType){
        VehicleTypeDto vehicleTypeDto = new VehicleTypeDto();
        vehicleTypeDto.setType(vehicleType.getType());
        vehicleTypeDto.setId(vehicleType.getId());
        return vehicleTypeDto;
    }

    public static VehicleType vehicleTypeCreateDtoToVehicleType(VehicleTypeCreateDto vehicleTypeCreateDto){
        VehicleType vehicleType = new VehicleType();
        vehicleType.setType(vehicleTypeCreateDto.getType());
        return vehicleType;
    }
}
