package org.komponente.rentalservice.rentalservicetest;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.Test;
import org.komponente.dto.carrental.CompanyCarCreateDto;
import org.komponente.dto.reservation.ActiveReservationCreateDto;
import org.komponente.dto.user.UserDto;
import org.komponente.rentalservice.domain.ActiveReservation;
import org.komponente.rentalservice.domain.CompanyCar;
import org.komponente.rentalservice.exceptions.CarNotAvaliableException;
import org.komponente.rentalservice.exceptions.CarNotFoundException;
import org.komponente.rentalservice.mapper.CompanyCarMapper;
import org.komponente.rentalservice.repository.ActiveReservationRepository;
import org.komponente.rentalservice.repository.CompanyCarRepository;
import org.komponente.rentalservice.service.EmailService;
import org.komponente.rentalservice.service.NormalTokenService;
import org.komponente.rentalservice.service.RentalService;
import org.mockito.ArgumentMatchers;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.*;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.RestTemplate;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@SpringBootTest
@ExtendWith({MockitoExtension.class, SpringExtension.class})
@MockitoSettings(strictness = Strictness.LENIENT)
public class RentalServiceReservationUnitTest {

    @MockBean
    private NormalTokenService tokenService;
    @MockBean
    private CompanyCarRepository companyCarRepository;
    @MockBean
    private ActiveReservationRepository activeReservationRepository;
    @MockBean
    private RestTemplate restTemplate;
    @MockBean
    private EmailService emailService;

    @Autowired
    private RentalService rentalService;

    @Test
    public void successfulTest(){
        String authorization = "testtoken";

        ActiveReservationCreateDto activeReservationCreateDto = new ActiveReservationCreateDto();
        activeReservationCreateDto.setCompanycarid(1L);
        activeReservationCreateDto.setBegindate(LocalDate.of(2023, 1, 1));
        activeReservationCreateDto.setEnddate(LocalDate.of(2023, 1, 5));
        Claims claims = mock(Claims.class);
        when(claims.get("id")).thenReturn(1L);
        when(claims.get("role")).thenReturn("ROLE_CLIENT");

        when(tokenService.parseToken(authorization)).thenReturn(claims);

        CompanyCarCreateDto companyCarCreateDto = new CompanyCarCreateDto();
        companyCarCreateDto.setCompanyid(1L);
        companyCarCreateDto.setNumberofcars(5);
        companyCarCreateDto.setPrice(1000L);
        companyCarCreateDto.setVehicleid(1L);
        CompanyCar companyCar = CompanyCarMapper.companyCarCreateDtoToCompanyCar(companyCarCreateDto);

        //when(restTemplate.getForObject("http://localhost:8081/api/companycar/1", CompanyCarCreateDto.class)).thenReturn(companyCarCreateDto);

        when(companyCarRepository.findById(1L)).thenReturn(java.util.Optional.of(companyCar));

        when(activeReservationRepository.findAllActiveReservationByCompanyCar(companyCar)).thenReturn(new java.util.ArrayList<ActiveReservation>());

        when(activeReservationRepository.save(any(ActiveReservation.class))).thenReturn(new ActiveReservation());

        //when(emailService.sendMessage(any(Serializable.class), "d")).thenReturn(true);
        doNothing().when(emailService).sendMessage(any(Serializable.class), anyString());

        ResponseEntity<Long> answer = new ResponseEntity<Long>(10L, HttpStatus.ACCEPTED);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", authorization);

        //when(restTemplate.exchange(any(), any(), any(), eq(Long.class))).thenReturn(answer);
        //when(restTemplate.exchange(eq("http://localhost:8081/api/rank/null"), eq(HttpMethod.GET), any(HttpEntity.class), eq(Long.class))).thenReturn( new ResponseEntity<Long>(10L, HttpStatus.ACCEPTED));

        UserDto userDto = new UserDto();
        userDto.setEmail("test@gmail.com");
        userDto.setId(1L);
        when(restTemplate.exchange(
                ArgumentMatchers.startsWith("http://localhost:8081/api/user"),
                ArgumentMatchers.any(HttpMethod.class),
                ArgumentMatchers.any(HttpEntity.class),
                ArgumentMatchers.<Class<UserDto>>any()
        )).thenReturn( new ResponseEntity<UserDto>(userDto, HttpStatus.ACCEPTED));

        when(restTemplate.exchange(
                ArgumentMatchers.startsWith("http://localhost:8081/api/rank"),
                ArgumentMatchers.any(HttpMethod.class),
                ArgumentMatchers.any(HttpEntity.class),
                ArgumentMatchers.<Class<Long>>any()
        )).thenReturn( new ResponseEntity<Long>(10L, HttpStatus.ACCEPTED));


        rentalService.reserveVehicle(activeReservationCreateDto, authorization);

        verify(activeReservationRepository, times(1)).save(any(ActiveReservation.class));
        verify(emailService, times(2)).sendMessage(any(Serializable.class), anyString());
        verify(restTemplate, times(1)).exchange(
                ArgumentMatchers.startsWith("http://localhost:8081/api/rank"),
                ArgumentMatchers.any(HttpMethod.class),
                ArgumentMatchers.any(HttpEntity.class),
                ArgumentMatchers.<Class<Long>>any()
        );
        verify(restTemplate, times(2)).exchange(
                ArgumentMatchers.startsWith("http://localhost:8081/api/user"),
                ArgumentMatchers.any(HttpMethod.class),
                ArgumentMatchers.any(HttpEntity.class),
                ArgumentMatchers.<Class<UserDto>>any()
        );
    }

    @Test
    public void noCarsAvailableTest(){
        String authorization = "testtoken";

        ActiveReservationCreateDto activeReservationCreateDto = new ActiveReservationCreateDto();
        activeReservationCreateDto.setCompanycarid(1L);
        activeReservationCreateDto.setBegindate(LocalDate.of(2023, 1, 1));
        activeReservationCreateDto.setEnddate(LocalDate.of(2023, 1, 5));
        Claims claims = mock(Claims.class);
        when(claims.get("id")).thenReturn(1L);
        when(claims.get("role")).thenReturn("ROLE_CLIENT");

        when(tokenService.parseToken(authorization)).thenReturn(claims);

        CompanyCarCreateDto companyCarCreateDto = new CompanyCarCreateDto();
        companyCarCreateDto.setCompanyid(1L);
        companyCarCreateDto.setNumberofcars(1);
        companyCarCreateDto.setPrice(1000L);
        companyCarCreateDto.setVehicleid(1L);
        CompanyCar companyCar = CompanyCarMapper.companyCarCreateDtoToCompanyCar(companyCarCreateDto);

        //when(restTemplate.getForObject("http://localhost:8081/api/companycar/1", CompanyCarCreateDto.class)).thenReturn(companyCarCreateDto);

        when(companyCarRepository.findById(1L)).thenReturn(java.util.Optional.of(companyCar));

        ActiveReservation activeReservation = new ActiveReservation();
        activeReservation.setClientId(5L);
        activeReservation.setCompanyCar(companyCar);
        activeReservation.setBegindate(LocalDate.of(2022, 12, 31));
        activeReservation.setEnddate(LocalDate.of(2023, 1, 10));
        List<ActiveReservation> ar = new ArrayList<>();
        ar.add(activeReservation);
        when(activeReservationRepository.findAllActiveReservationByCompanyCar(companyCar)).thenReturn(ar);

        assertThrows(CarNotAvaliableException.class, () -> rentalService.reserveVehicle(activeReservationCreateDto, authorization));

    }

    @Test
    public void companyCarNotFound(){
        String authorization = "testtoken";

        ActiveReservationCreateDto activeReservationCreateDto = new ActiveReservationCreateDto();
        activeReservationCreateDto.setCompanycarid(1L);
        activeReservationCreateDto.setBegindate(LocalDate.of(2023, 1, 1));
        activeReservationCreateDto.setEnddate(LocalDate.of(2023, 1, 5));
        Claims claims = mock(Claims.class);
        when(claims.get("id")).thenReturn(1L);
        when(claims.get("role")).thenReturn("ROLE_CLIENT");

        when(tokenService.parseToken(authorization)).thenReturn(claims);


        when(companyCarRepository.findById(1L)).thenReturn(java.util.Optional.ofNullable(null));

        assertThrows(CarNotFoundException.class, () -> rentalService.reserveVehicle(activeReservationCreateDto, authorization));
    }
}
