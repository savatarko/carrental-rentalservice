package org.komponente.rentalservice.controller;

import io.jsonwebtoken.Jwts;
import lombok.AllArgsConstructor;
import org.komponente.dto.carrental.CompanyCarDto;
import org.komponente.dto.requests.CarSearchFilterDto;
import org.komponente.dto.reservation.ActiveReservationCreateDto;
import org.komponente.dto.review.ReviewDto;
import org.komponente.rentalservice.security.CheckSecurity;
import org.komponente.rentalservice.security.token.TokenService;
import org.komponente.rentalservice.service.RentalService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/rent")
public class RentalController {
    private RentalService rentalService;
    private TokenService tokenService;

    public RentalController(RentalService rentalService) {
        this.rentalService = rentalService;
    }

    @GetMapping("/find")
    @CheckSecurity(roles = {"ROLE_ADMIN", "ROLE_MANAGER", "ROLE_CLIENT"})
    public ResponseEntity<?> searchVehicles(@RequestHeader("Authorization") String authorization, @RequestBody @Valid CarSearchFilterDto carSearchFilterDto){
        return new ResponseEntity<>(rentalService.searchVehicles(carSearchFilterDto), HttpStatus.OK);
    }

    @PostMapping("/reserve}")
    @CheckSecurity(roles = {"ROLE_CLIENT"})
    public ResponseEntity<?> rentVehicle(@RequestHeader("Authorization") String authorization, @RequestBody @Valid ActiveReservationCreateDto newres){
        Long id = tokenService.parseToken(authorization).get("id", Long.class);
        return new ResponseEntity<>(rentalService.reserveVehicle(newres, id), HttpStatus.OK);
    }

    @PutMapping("/review/{rentId}")
    @CheckSecurity(roles = {"ROLE_CLIENT"})
    public ResponseEntity<?> leaveAReview(@RequestHeader("Authorization") String authorization,@PathVariable Long rentId, @RequestBody @Valid ReviewDto reviewDto){

        return new ResponseEntity<>(rentalService.leaveAReview(rentId,tokenService.parseToken(authorization).get("id", Long.class), reviewDto) ,HttpStatus.OK);
    }

    @DeleteMapping("/cancel/{rentId}")
    @CheckSecurity(roles = {"ROLE_CLIENT", "ROLE_MANAGER"})
    public ResponseEntity<?> cancelReservation(@RequestHeader("Authorization") String authorization,@PathVariable Long rentId){

        rentalService.cancelReservation(rentId, tokenService.parseToken(authorization));
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
