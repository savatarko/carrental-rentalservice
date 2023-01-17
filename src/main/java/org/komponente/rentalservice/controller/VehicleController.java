package org.komponente.rentalservice.controller;

import lombok.AllArgsConstructor;
import org.komponente.dto.carrental.CompanyCarChangeDto;
import org.komponente.dto.carrental.CompanyCarCreateDto;
import org.komponente.dto.requests.CarSearchFilterDto;
import org.komponente.dto.vehicle.VehicleCreateDto;
import org.komponente.dto.vehicle.VehicleTypeCreateDto;
import org.komponente.rentalservice.security.CheckSecurity;
import org.komponente.rentalservice.service.VehicleService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@AllArgsConstructor
@RestController
@RequestMapping("/vehicle")
public class VehicleController {
    /*



    CompanyCarDto changePrice(CompanyCarChangeDto companyCarChangeDto);
     */

    private VehicleService vehicleService;

    @PostMapping("/createtype")
    @CheckSecurity(roles = {"ROLE_MANAGER"})
    public ResponseEntity<?> createVehicleType(@RequestHeader("Authorization") String authorization, @RequestBody @Valid VehicleTypeCreateDto vehicleTypeCreateDto){
        return new ResponseEntity<>(vehicleService.createVehicleType(vehicleTypeCreateDto), HttpStatus.OK);
    }

    @PostMapping("/create")
    @CheckSecurity(roles = {"ROLE_MANAGER"})
    public ResponseEntity<?> createVehicle(@RequestHeader("Authorization") String authorization, @RequestBody @Valid VehicleCreateDto vehicleTypeCreateDto){
        return new ResponseEntity<>(vehicleService.createVehicle(vehicleTypeCreateDto), HttpStatus.OK);
    }

    @PostMapping("/assign")
    @CheckSecurity(roles = {"ROLE_MANAGER"})
    public ResponseEntity<?> createCompanyCar(@RequestHeader("Authorization") String authorization, @RequestBody @Valid CompanyCarCreateDto companyCarCreateDto){
        return new ResponseEntity<>(vehicleService.createCompanyCar(companyCarCreateDto), HttpStatus.OK);
    }

    @PutMapping("/update")
    @CheckSecurity(roles = {"ROLE_MANAGER"})
    public ResponseEntity<?> updateCompanyCar(@RequestHeader("Authorization") String authorization, @RequestBody @Valid CompanyCarChangeDto companyCarChangeDto){
        return new ResponseEntity<>(vehicleService.changePrice(companyCarChangeDto), HttpStatus.OK);
    }
}
