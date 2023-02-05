package org.komponente.rentalservice.rentalservicetest;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.komponente.dto.client.ClientCreateDto;
import org.komponente.dto.client.ClientDto;
import org.komponente.dto.manager.ManagerCreateDto;
import org.komponente.dto.manager.ManagerDto;
import org.komponente.dto.reservation.ActiveReservationCreateDto;
import org.komponente.dto.token.TokenRequestDto;
import org.komponente.rentalservice.domain.*;
import org.komponente.rentalservice.mapper.ActiveReservationMapper;
import org.komponente.rentalservice.repository.*;
import org.komponente.rentalservice.service.EmailService;
import org.komponente.rentalservice.service.RentalService;
import org.mockito.ArgumentMatchers;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.Serializable;
import java.time.LocalDate;

import static org.mockito.Mockito.doNothing;

@SpringBootTest
@ExtendWith({SpringExtension.class, MockitoExtension.class})
@MockitoSettings(strictness = Strictness.LENIENT)
public class RentalServiceCancelntegrationTest {
    @Autowired
    private RentalService rentalService;

    @Autowired
    private VehicleTypeRepository vehicleTypeRepository;
    @Autowired
    private VehicleRepository vehicleRepository;
    @Autowired
    private CompanyRepository companyRepository;
    @Autowired
    private CompanyCarRepository companyCarRepository;

    @Autowired
    private ActiveReservationRepository activeReservationRepository;

    @MockBean
    private EmailService emailService;

    /*
    public RentalServiceReservationIntegrationTest(RentalService rentalService, VehicleTypeRepository vehicleTypeRepository, VehicleRepository vehicleRepository, CompanyRepository companyRepository, CompanyCarRepository companyCarRepository) {
        this.rentalService = rentalService;
        this.vehicleTypeRepository = vehicleTypeRepository;
        this.vehicleRepository = vehicleRepository;
        this.companyRepository = companyRepository;
        this.companyCarRepository = companyCarRepository;
    }

     */

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
    private ManagerDto createNewManager(Long companyId){
        String path = "http://localhost:8081/api/user/register/client";
        RestTemplate restTemplate = new RestTemplate();
        ManagerCreateDto clientCreateDto = new ManagerCreateDto();
        clientCreateDto.setEmail("test");
        clientCreateDto.setPassword("test");
        clientCreateDto.setSurname("test");
        clientCreateDto.setName("test");
        clientCreateDto.setNumber("1");
        clientCreateDto.setDateofbirth(LocalDate.of(1999, 1, 1));
        clientCreateDto.setUsername("testuser");
        clientCreateDto.setCompanyId(companyId);
        clientCreateDto.setDateofemployment(LocalDate.of(2009, 1, 1));
        try{
            return restTemplate.postForObject(path, clientCreateDto, ManagerDto.class);
        }
        catch (HttpClientErrorException e){
            e.printStackTrace();
        }
        return null;
    }
    private void deleteUser(String token){
        String path = "http://localhost:8081/api/user/delete";
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", token);
        try{
            restTemplate.exchange(path, HttpMethod.PUT, new HttpEntity<>(headers), Void.class);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
    private String getToken(ClientDto clientDto){
        String path = "http://localhost:8081/api/user/login";
        RestTemplate restTemplate = new RestTemplate();
        TokenRequestDto tokenRequestDto = new TokenRequestDto();
        tokenRequestDto.setPassword("test");
        tokenRequestDto.setUsername(clientDto.getUsername());
        try{
            String token = restTemplate.postForObject(path, tokenRequestDto, String.class);
            return token;
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
    private String getToken(ManagerDto clientDto){
        String path = "http://localhost:8081/api/user/login";
        RestTemplate restTemplate = new RestTemplate();
        TokenRequestDto tokenRequestDto = new TokenRequestDto();
        tokenRequestDto.setPassword("test");
        tokenRequestDto.setUsername(clientDto.getUsername());
        try{
            String token = restTemplate.postForObject(path, tokenRequestDto, String.class);
            return token;
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    @Test
    public void testManager(){

        ClientDto clientDto = createNewUser();
        String token = getToken(clientDto);

        //creating company car
        VehicleType vehicleType = new VehicleType();
        vehicleType.setType("testtype");
        vehicleTypeRepository.save(vehicleType);

        Vehicle vehicle = new Vehicle();
        vehicle.setName("testvehicle");
        vehicle.setVehicleType(vehicleType);
        vehicleRepository.save(vehicle);

        Company company = new Company();
        company.setName("testcompany");
        company.setDescription("testdescription");
        company.setManagerid(0L);
        companyRepository.save(company);

        ManagerDto managerDto = createNewManager(company.getId());
        company.setManagerid(managerDto.getId());
        companyRepository.save(company);

        CompanyCar companyCar = new CompanyCar();
        companyCar.setCompany(company);
        companyCar.setVehicle(vehicle);
        companyCar.setPrice(1000L);
        companyCar.setNumberofcars(5);
        companyCarRepository.save(companyCar);


        ActiveReservationCreateDto activeReservationCreateDto = new ActiveReservationCreateDto();
        activeReservationCreateDto.setCompanycarid(companyCar.getId());
        activeReservationCreateDto.setBegindate(LocalDate.of(2023, 1, 1));
        activeReservationCreateDto.setEnddate(LocalDate.of(2023, 1, 5));
        ActiveReservation activeReservation = ActiveReservationMapper.activeReservationCreateDtoToActiveReservation(activeReservationCreateDto);
        activeReservationRepository.save(activeReservation);

        doNothing().when(emailService).sendMessage(
                ArgumentMatchers.any(Serializable.class),
                ArgumentMatchers.anyString()
        );

        rentalService.cancelReservation(activeReservation.getId(), token);

        vehicleTypeRepository.delete(vehicleType);
        vehicleRepository.delete(vehicle);
        companyRepository.delete(company);
        companyCarRepository.delete(companyCar);
        deleteUser(token);
        deleteUser(getToken(managerDto));
    }
}
