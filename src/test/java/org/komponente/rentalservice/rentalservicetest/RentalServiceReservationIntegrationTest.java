package org.komponente.rentalservice.rentalservicetest;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.komponente.dto.carrental.CompanyCarCreateDto;
import org.komponente.dto.client.ClientCreateDto;
import org.komponente.dto.client.ClientDto;
import org.komponente.dto.reservation.ActiveReservationCreateDto;
import org.komponente.dto.token.TokenRequestDto;
import org.komponente.dto.user.UserDto;
import org.komponente.rentalservice.domain.CompanyCar;
import org.komponente.rentalservice.mapper.CompanyCarMapper;
import org.komponente.rentalservice.service.RentalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@ExtendWith({SpringExtension.class})
public class RentalServiceReservationIntegrationTest {

    @Autowired
    private RentalService rentalService;

    //TODO not impplemented yet

    private ClientDto createNewUser(){
        String path = "http://localhost:8081/api/user/register/client";
        RestTemplate restTemplate = new RestTemplate();
        ClientCreateDto clientCreateDto = new ClientCreateDto();
        clientCreateDto.setEmail("test");
        clientCreateDto.setPassword("test");
        clientCreateDto.setSurname("test");
        clientCreateDto.setName("test");
        clientCreateDto.setNumber("1");
        clientCreateDto.setDateofbirth(LocalDate.of(1999, 1, 1));
        clientCreateDto.setUsername("testuser");
        clientCreateDto.setPassport("testpassport");
        try{
            ClientDto clientDto = restTemplate.postForObject(path, clientCreateDto, ClientDto.class);
            return clientDto;
        }
        catch (HttpClientErrorException e){
            e.printStackTrace();
        }
        return null;
    }
    private String getToken(ClientDto clientDto){
        String path = "http://localhost:8081/api/user/login";
        RestTemplate restTemplate = new RestTemplate();
        TokenRequestDto tokenRequestDto = new TokenRequestDto();
        tokenRequestDto.setPassword("test");
        tokenRequestDto.setUsername(clientDto.getUsername());
        try{
            String token = restTemplate.postForObject(path, clientDto, String.class);
            return token;
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    @Test
    public void test(){

        ClientDto clientDto = createNewUser();
        String token = getToken(clientDto);

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
