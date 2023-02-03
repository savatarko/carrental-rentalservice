package org.komponente.rentalservice.rentalservicetest;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.komponente.dto.carrental.CompanyCarCreateDto;
import org.komponente.dto.reservation.ActiveReservationCreateDto;
import org.komponente.dto.user.UserDto;
import org.komponente.rentalservice.domain.CompanyCar;
import org.komponente.rentalservice.mapper.CompanyCarMapper;
import org.komponente.rentalservice.service.RentalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@ExtendWith({SpringExtension.class})
public class RentalServiceReservationIntegrationTest {

    @Autowired
    private RentalService rentalService;

    //TODO not impplemented yet
    @Test
    public void test(){
        String authorization = "testtoken";

        ActiveReservationCreateDto activeReservationCreateDto = new ActiveReservationCreateDto();
        activeReservationCreateDto.setCompanycarid(1L);
        activeReservationCreateDto.setBegindate(LocalDate.of(2023, 1, 1));
        activeReservationCreateDto.setEnddate(LocalDate.of(2023, 1, 5));
        Claims claims = mock(Claims.class);
        when(claims.get("id")).thenReturn(1L);
        when(claims.get("role")).thenReturn("ROLE_CLIENT");


        CompanyCarCreateDto companyCarCreateDto = new CompanyCarCreateDto();
        companyCarCreateDto.setCompanyid(1L);
        companyCarCreateDto.setNumberofcars(5);
        companyCarCreateDto.setPrice(1000L);
        companyCarCreateDto.setVehicleid(1L);
        CompanyCar companyCar = CompanyCarMapper.companyCarCreateDtoToCompanyCar(companyCarCreateDto);


        ResponseEntity<Long> answer = new ResponseEntity<Long>(10L, HttpStatus.ACCEPTED);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", authorization);


        UserDto userDto = new UserDto();
        userDto.setEmail("test@gmail.com");
        userDto.setId(1L);

        rentalService.reserveVehicle(activeReservationCreateDto, authorization);
    }
}
