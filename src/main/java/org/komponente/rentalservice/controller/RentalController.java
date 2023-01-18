package org.komponente.rentalservice.controller;

import io.jsonwebtoken.Jwts;
import lombok.AllArgsConstructor;
import org.komponente.dto.carrental.CompanyCarDto;
import org.komponente.dto.requests.CarSearchFilterDto;
import org.komponente.dto.reservation.ActiveReservationCreateDto;
import org.komponente.dto.review.ReviewDto;
import org.komponente.rentalservice.security.CheckSecurity;
import org.komponente.rentalservice.security.token.TokenService;
import org.komponente.rentalservice.security.token.TokenServiceImpl;
import org.komponente.rentalservice.service.NormalTokenService;
import org.komponente.rentalservice.service.RentalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

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

    @GetMapping("/find")
    @CheckSecurity(roles = {"ROLE_ADMIN", "ROLE_MANAGER", "ROLE_CLIENT"})
    public ResponseEntity<?> searchVehicles(@RequestHeader("Authorization") String authorization, @RequestBody @Valid CarSearchFilterDto carSearchFilterDto){
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
    @CheckSecurity(roles = {"ROLE_CLIENT", "ROLE_MANAGER"})
    public ResponseEntity<?> cancelReservation(@RequestHeader("Authorization") String authorization,@PathVariable Long rentId){
        rentalService.cancelReservation(rentId, authorization);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
