package org.komponente.rentalservice.rentalservicetest;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.komponente.dto.carrental.CompanyCarCreateDto;
import org.komponente.dto.user.UserDto;
import org.komponente.rentalservice.domain.ActiveReservation;
import org.komponente.rentalservice.domain.Company;
import org.komponente.rentalservice.domain.CompanyCar;
import org.komponente.rentalservice.domain.Vehicle;
import org.komponente.rentalservice.exceptions.UnauthorizedException;
import org.komponente.rentalservice.mapper.CompanyCarMapper;
import org.komponente.rentalservice.repository.ActiveReservationRepository;
import org.komponente.rentalservice.repository.CompanyCarRepository;
import org.komponente.rentalservice.repository.CompanyRepository;
import org.komponente.rentalservice.repository.VehicleRepository;
import org.komponente.rentalservice.service.EmailService;
import org.komponente.rentalservice.service.NormalTokenService;
import org.komponente.rentalservice.service.RentalService;
import org.mockito.ArgumentMatchers;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@SpringBootTest
@ExtendWith({org.mockito.junit.jupiter.MockitoExtension.class, org.springframework.test.context.junit.jupiter.SpringExtension.class})
@MockitoSettings(strictness = Strictness.LENIENT)
public class RentalServiceCancelUnitTest {
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

    @MockBean
    private CompanyRepository companyRepository;

    @MockBean
    private VehicleRepository vehicleRepository;

    @Autowired
    private RentalService rentalService;

    @Test
    public void cancelReservationClientTest() {
        Long rentalId = 1L;
        String authorization = "testtoken";
        Claims claims = mock(Claims.class);
        when(claims.get("id", Long.class)).thenReturn(1L);
        when(claims.get("role", String.class)).thenReturn("ROLE_CLIENT");

        when(tokenService.parseToken(authorization)).thenReturn(claims);

        CompanyCarCreateDto companyCarCreateDto = new CompanyCarCreateDto();
        companyCarCreateDto.setCompanyid(1L);
        companyCarCreateDto.setNumberofcars(5);
        companyCarCreateDto.setPrice(1000L);
        companyCarCreateDto.setVehicleid(1L);

        Company company = new Company();
        company.setId(1L);
        Vehicle vehicle = new Vehicle();
        vehicle.setId(1L);

        when(companyRepository.findById(1L)).thenReturn(Optional.of(company));
        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(vehicle));

        CompanyCar companyCar = CompanyCarMapper.companyCarCreateDtoToCompanyCar(companyCarCreateDto);

        ActiveReservation activeReservation = new ActiveReservation();
        activeReservation.setClientId(1L);
        activeReservation.setBegindate(LocalDate.of(2023, 1, 1));
        activeReservation.setEnddate(LocalDate.of(2023, 1, 5));
        activeReservation.setCompanyCar(companyCar);
        when(activeReservationRepository.findById(rentalId)).thenReturn(java.util.Optional.of(activeReservation));
        doNothing().when(activeReservationRepository).delete(activeReservation);

        UserDto userDto = new UserDto();
        userDto.setEmail("test@gmail.com");
        userDto.setId(1L);
        when(restTemplate.exchange(
                ArgumentMatchers.startsWith("http://localhost:8081/api/user"),
                ArgumentMatchers.any(HttpMethod.class),
                ArgumentMatchers.any(HttpEntity.class),
                ArgumentMatchers.<Class<UserDto>>any()
        )).thenReturn( new ResponseEntity<UserDto>(userDto, HttpStatus.ACCEPTED));

        doNothing().when(emailService).sendMessage(any(Serializable.class), anyString());

        rentalService.cancelReservation(rentalId, authorization);

        verify(activeReservationRepository, times(1)).delete(activeReservation);
        verify(emailService, times(2)).sendMessage(any(Serializable.class), anyString());
        verify(restTemplate, times(2)).exchange(
                ArgumentMatchers.startsWith("http://localhost:8081/api/user"),
                ArgumentMatchers.any(HttpMethod.class),
                ArgumentMatchers.any(HttpEntity.class),
                ArgumentMatchers.<Class<UserDto>>any()
        );
        verify(tokenService, times(1)).parseToken(authorization);
        verify(activeReservationRepository, times(1)).findById(rentalId);
    }

    @Test
    public void someoneElsesReservationTest(){
        Long rentalId = 1L;
        String authorization = "testtoken";
        Claims claims = mock(Claims.class);
        when(claims.get("id", Long.class)).thenReturn(1L);
        when(claims.get("role", String.class)).thenReturn("ROLE_CLIENT");

        when(tokenService.parseToken(authorization)).thenReturn(claims);

        CompanyCarCreateDto companyCarCreateDto = new CompanyCarCreateDto();
        companyCarCreateDto.setCompanyid(1L);
        companyCarCreateDto.setNumberofcars(5);
        companyCarCreateDto.setPrice(1000L);
        companyCarCreateDto.setVehicleid(1L);

        Company company = new Company();
        company.setId(1L);
        Vehicle vehicle = new Vehicle();
        vehicle.setId(1L);

        when(companyRepository.findById(1L)).thenReturn(Optional.of(company));
        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(vehicle));
        CompanyCar companyCar = CompanyCarMapper.companyCarCreateDtoToCompanyCar(companyCarCreateDto);

        ActiveReservation activeReservation = new ActiveReservation();
        activeReservation.setClientId(2L);
        activeReservation.setBegindate(LocalDate.of(2023, 1, 1));
        activeReservation.setEnddate(LocalDate.of(2023, 1, 5));
        activeReservation.setCompanyCar(companyCar);
        when(activeReservationRepository.findById(rentalId)).thenReturn(java.util.Optional.of(activeReservation));

        assertThrows(UnauthorizedException.class, () -> rentalService.cancelReservation(rentalId, authorization));

        verify(activeReservationRepository, times(1)).findById(rentalId);
    }

    @Test
    public void cancelReservationManagerTest(){
        Long rentalId = 1L;
        String authorization = "testtoken";
        Claims claims = mock(Claims.class);
        when(claims.get("id", Long.class)).thenReturn(10L);
        when(claims.get("role", String.class)).thenReturn("ROLE_MANAGER");

        when(tokenService.parseToken(authorization)).thenReturn(claims);

        CompanyCarCreateDto companyCarCreateDto = new CompanyCarCreateDto();
        companyCarCreateDto.setCompanyid(5L);
        companyCarCreateDto.setNumberofcars(5);
        companyCarCreateDto.setPrice(1000L);
        companyCarCreateDto.setVehicleid(1L);

        Company company = new Company();
        company.setId(5L);
        Vehicle vehicle = new Vehicle();
        vehicle.setId(1L);

        when(companyRepository.findById(5L)).thenReturn(Optional.of(company));
        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(vehicle));

        CompanyCar companyCar = CompanyCarMapper.companyCarCreateDtoToCompanyCar(companyCarCreateDto);

        ActiveReservation activeReservation = new ActiveReservation();
        activeReservation.setClientId(1L);
        activeReservation.setBegindate(LocalDate.of(2023, 1, 1));
        activeReservation.setEnddate(LocalDate.of(2023, 1, 5));
        activeReservation.setCompanyCar(companyCar);
        when(activeReservationRepository.findById(rentalId)).thenReturn(java.util.Optional.of(activeReservation));
        doNothing().when(activeReservationRepository).delete(activeReservation);

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
                ArgumentMatchers.startsWith("http://localhost:8081/api/user/manager"),
                ArgumentMatchers.any(HttpMethod.class),
                ArgumentMatchers.any(HttpEntity.class),
                ArgumentMatchers.<Class<Long>>any()
        )).thenReturn( new ResponseEntity<Long>(5L, HttpStatus.ACCEPTED));

        doNothing().when(emailService).sendMessage(any(Serializable.class), anyString());

        rentalService.cancelReservation(rentalId, authorization);

        verify(activeReservationRepository, times(1)).delete(activeReservation);
        verify(emailService, times(2)).sendMessage(any(Serializable.class), anyString());
        verify(restTemplate, times(3)).exchange(
                ArgumentMatchers.startsWith("http://localhost:8081/api/user"),
                ArgumentMatchers.any(HttpMethod.class),
                ArgumentMatchers.any(HttpEntity.class),
                ArgumentMatchers.<Class<UserDto>>any()
        );
        verify(restTemplate, times(1)).exchange(
                ArgumentMatchers.startsWith("http://localhost:8081/api/user/manager"),
                ArgumentMatchers.any(HttpMethod.class),
                ArgumentMatchers.any(HttpEntity.class),
                ArgumentMatchers.<Class<Long>>any()
        );

        verify(tokenService, times(1)).parseToken(authorization);
        verify(activeReservationRepository, times(1)).findById(rentalId);
    }

    @Test
    public void wrongManagerTest(){
        Long rentalId = 1L;
        String authorization = "testtoken";
        Claims claims = mock(Claims.class);
        when(claims.get("id", Long.class)).thenReturn(1L);
        when(claims.get("role", String.class)).thenReturn("ROLE_MANAGER");

        when(tokenService.parseToken(authorization)).thenReturn(claims);

        CompanyCarCreateDto companyCarCreateDto = new CompanyCarCreateDto();
        companyCarCreateDto.setCompanyid(1L);
        companyCarCreateDto.setNumberofcars(5);
        companyCarCreateDto.setPrice(1000L);
        companyCarCreateDto.setVehicleid(1L);

        Company company = new Company();
        company.setId(1L);
        Vehicle vehicle = new Vehicle();
        vehicle.setId(1L);

        when(companyRepository.findById(1L)).thenReturn(Optional.of(company));
        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(vehicle));
        CompanyCar companyCar = CompanyCarMapper.companyCarCreateDtoToCompanyCar(companyCarCreateDto);

        ActiveReservation activeReservation = new ActiveReservation();
        activeReservation.setClientId(1L);
        activeReservation.setBegindate(LocalDate.of(2023, 1, 1));
        activeReservation.setEnddate(LocalDate.of(2023, 1, 5));
        activeReservation.setCompanyCar(companyCar);
        when(activeReservationRepository.findById(rentalId)).thenReturn(java.util.Optional.of(activeReservation));

        when(restTemplate.exchange(
                ArgumentMatchers.startsWith("http://localhost:8081/api/user"),
                ArgumentMatchers.any(HttpMethod.class),
                ArgumentMatchers.any(HttpEntity.class),
                ArgumentMatchers.<Class<Long>>any()
        )).thenReturn( new ResponseEntity<Long>(5L, HttpStatus.ACCEPTED));

        assertThrows(UnauthorizedException.class, () -> rentalService.cancelReservation(rentalId, authorization));

        verify(activeReservationRepository, times(1)).findById(rentalId);
        verify(restTemplate, times(1)).exchange(
                ArgumentMatchers.startsWith("http://localhost:8081/api/user"),
                ArgumentMatchers.any(HttpMethod.class),
                ArgumentMatchers.any(HttpEntity.class),
                ArgumentMatchers.<Class<Long>>any()
        );
    }
}
