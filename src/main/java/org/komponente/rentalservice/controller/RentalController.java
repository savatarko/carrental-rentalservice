package org.komponente.rentalservice.controller;

import io.jsonwebtoken.Jwts;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.komponente.dto.carrental.CompanyCarDto;
import org.komponente.dto.completedrental.CompletedRentalDto;
import org.komponente.dto.requests.CarSearchFilterDto;
import org.komponente.dto.reservation.ActiveReservationCreateDto;
import org.komponente.dto.reservation.ActiveReservationDto;
import org.komponente.dto.review.ReviewDto;
import org.komponente.dto.user.UserDto;
import org.komponente.dto.vehicle.VehicleDto;
import org.komponente.rentalservice.security.CheckSecurity;
import org.komponente.rentalservice.security.token.TokenService;
import org.komponente.rentalservice.security.token.TokenServiceImpl;
import org.komponente.rentalservice.service.NormalTokenService;
import org.komponente.rentalservice.service.RentalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/rent")
public class RentalController {
    private RentalService rentalService;
    //private TokenService tokenService;
    @Autowired
    private NormalTokenService normalTokenService;

    /*
    public RentalController(RentalService rentalService, TokenService tokenService) {
        this.rentalService = rentalService;
        this.tokenService = tokenService;
    }

     */

    @PutMapping("/find")
    @CheckSecurity(roles = {"ROLE_ADMIN", "ROLE_MANAGER", "ROLE_CLIENT"})
    public ResponseEntity<List<CompanyCarDto>> searchVehicles(@RequestHeader("Authorization") String authorization, @RequestBody @Valid CarSearchFilterDto carSearchFilterDto){
        return new ResponseEntity<>(rentalService.searchVehicles(carSearchFilterDto), HttpStatus.OK);
    }

    @PostMapping("/reserve")
    //@CheckSecurity(roles = {"ROLE_CLIENT"})
    public ResponseEntity<?> rentVehicle(@RequestHeader("Authorization") String authorization, @RequestBody @Valid ActiveReservationCreateDto newres){
        //Long id = normalTokenService.parseToken(authorization).get("id", Long.class);
        return new ResponseEntity<>(rentalService.reserveVehicle(newres, authorization), HttpStatus.OK);
    }

    @PutMapping("/review")
    @CheckSecurity(roles = {"ROLE_CLIENT"})
    public ResponseEntity<?> leaveAReview(@RequestHeader("Authorization") String authorization, @RequestBody @Valid ReviewDto reviewDto){

        return new ResponseEntity<>(rentalService.leaveAReview(authorization, reviewDto) ,HttpStatus.OK);
    }

    @DeleteMapping("/cancel/{rentId}")
    //@CheckSecurity(roles = {"ROLE_CLIENT", "ROLE_MANAGER"})
    public ResponseEntity<?> cancelReservation(@RequestHeader("Authorization") String authorization,@PathVariable Long rentId){
        System.out.println("test");
        rentalService.cancelReservation(rentId, authorization);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ApiOperation("Get all reservations for current user")
    @GetMapping("/active")
    @CheckSecurity(roles = {"ROLE_CLIENT", "ROLE_MANAGER"})
    public ResponseEntity<List<ActiveReservationDto>> getActiveReservations(@RequestHeader("Authorization") String authorization){
        return new ResponseEntity<>(rentalService.getMyCurrentReservations(authorization), HttpStatus.OK);
    }

    @ApiOperation("Get all completed reservations for current user")
    @GetMapping("/completed")
    @CheckSecurity(roles = {"ROLE_CLIENT", "ROLE_MANAGER"})
    public ResponseEntity<List<CompletedRentalDto>> getCompletedReservations(@RequestHeader("Authorization") String authorization){
        return new ResponseEntity<>(rentalService.getMyCompletedReservations(authorization), HttpStatus.OK);
    }

}
