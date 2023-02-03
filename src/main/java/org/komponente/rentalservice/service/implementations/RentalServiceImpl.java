package org.komponente.rentalservice.service.implementations;

import io.github.resilience4j.retry.Retry;
import io.jsonwebtoken.Claims;
import lombok.AllArgsConstructor;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.catalina.Manager;
import org.komponente.dto.carrental.CompanyCarDto;
import org.komponente.dto.client.ClientDto;
import org.komponente.dto.completedrental.CompletedRentalDto;
import org.komponente.dto.email.CancelReservationClientNotification;
import org.komponente.dto.email.CancelReservationManagerNotification;
import org.komponente.dto.email.SuccessfulReservationClientNotification;
import org.komponente.dto.email.SuccessfulReservationManagerNotification;
import org.komponente.dto.manager.ManagerDto;
import org.komponente.dto.requests.CarSearchFilterDto;
import org.komponente.dto.reservation.ActiveReservationCreateDto;
import org.komponente.dto.reservation.ActiveReservationDto;
import org.komponente.dto.review.ReviewDto;
import org.komponente.dto.user.UserDto;
import org.komponente.dto.vehicle.VehicleDto;
import org.komponente.rentalservice.domain.*;
import org.komponente.rentalservice.exceptions.*;
import org.komponente.rentalservice.mapper.*;
import org.komponente.rentalservice.repository.*;
import org.komponente.rentalservice.security.token.TokenService;
import org.komponente.rentalservice.service.CompanyService;
import org.komponente.rentalservice.service.EmailService;
import org.komponente.rentalservice.service.NormalTokenService;
import org.komponente.rentalservice.service.RentalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import javax.jms.*;
import java.io.Serializable;
import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

//@AllArgsConstructor
@Service
public class RentalServiceImpl implements RentalService {
    private VehicleRepository vehicleRepository;
    private CompanyRepository companyRepository;
    private ActiveReservationRepository activeReservationRepository;
    private CompletedRentalRepository completedRentalRepository;
    private CompanyCarRepository companyCarRepository;
    private Retry userServiceRetry;
    private EmailService emailService;
    @Autowired
    private RestTemplate restTemplate;

    //@Autowired
    //private TokenService tokenService;

    @Autowired
    private NormalTokenService normalTokenService;

    public RentalServiceImpl(VehicleRepository vehicleRepository, CompanyRepository companyRepository, ActiveReservationRepository activeReservationRepository, CompletedRentalRepository completedRentalRepository, CompanyCarRepository companyCarRepository, Retry userServiceRetry, EmailService emailService, NormalTokenService normalTokenService) {
        this.vehicleRepository = vehicleRepository;
        this.companyRepository = companyRepository;
        this.activeReservationRepository = activeReservationRepository;
        this.completedRentalRepository = completedRentalRepository;
        this.companyCarRepository = companyCarRepository;
        this.userServiceRetry = userServiceRetry;
        this.emailService = emailService;
        this.normalTokenService = normalTokenService;
    }

    private final String userserviceurl = "http://localhost:8081/api";

    private void sendMessage(Serializable content, String queueName) {
        try {
            ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://localhost:61616");
            Connection connection = connectionFactory.createConnection();
            connection.start();
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Destination destination = new ActiveMQQueue(queueName);
            MessageProducer producer = session.createProducer(destination);
            ObjectMessage message = session.createObjectMessage(content);
            producer.send(message);
            producer.close();
            session.close();
            connection.close();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }


    @Override
    public List<CompanyCarDto> searchVehicles(CarSearchFilterDto carSearchFilterDto) {
        List<CompanyCarDto> output= new ArrayList<>();
        List<CompanyCar> vehicles = companyCarRepository.findAll();
        if(carSearchFilterDto.getCompanyname()!=null)
        {
            vehicles = vehicles.stream().filter(v->carSearchFilterDto.getCompanyname().contains(v.getCompany().getName())).collect(Collectors.toList());
        }
        if(carSearchFilterDto.getDateparam()!=null)
        {
            for(int i = 0;i<vehicles.size();i++)
            {
                CompanyCar v = vehicles.get(i);
                int start = v.getNumberofcars();
                List<ActiveReservation> rentlist = activeReservationRepository.findAllActiveReservationByCompanyCar(v);
                for(ActiveReservation rent: rentlist)
                {
                    if(rent.getEnddate().isAfter(carSearchFilterDto.getDateparam().getStartdate()))
                    {
                        start--;
                        if(start == 0) {
                            vehicles.remove(i);
                            i--;
                            break;
                        }
                    }
                    if(carSearchFilterDto.getDateparam().getStartdate().plusDays(carSearchFilterDto.getDateparam().getDuration()).isBefore(rent.getBegindate()))
                    {
                        start--;
                        if(start == 0) {
                            vehicles.remove(i);
                            i--;
                            break;
                        }
                    }
                }
            }
        }
        if(carSearchFilterDto.getSorted()!=0)
        {
            if(carSearchFilterDto.getSorted()==1) {
                vehicles = vehicles.stream().sorted((o1,o2)-> Math.toIntExact(o1.getPrice() - o2.getPrice())).collect(Collectors.toList());
            }
            else{
                vehicles = vehicles.stream().sorted((o1,o2)-> Math.toIntExact(o2.getPrice() - o1.getPrice())).collect(Collectors.toList());
            }
        }
        for(CompanyCar v :vehicles)
        {
            output.add(CompanyCarMapper.companyCarToCompanyCarDto(v));
        }
        return output;
    }
    private Long getRankDiscount(Claims claims, String authorization){
            String path = userserviceurl.concat("/rank/" + claims.get("id", Long.class));
            //RestTemplate restTemplate = new RestTemplate();
            //Long rankdiscount = restTemplate.getForObject(path, Long.class);
            HttpHeaders headers = new HttpHeaders();
            headers.add("Authorization", authorization);
            try{
            Long rankdiscount = restTemplate.exchange(path, HttpMethod.GET, new HttpEntity<>(headers), Long.class).getBody();
            return rankdiscount;
            }
            catch (HttpClientErrorException e){
                e.printStackTrace();
            }
            return null;
    }
    private UserDto getUserDto(Long id, String authorization){
        String path = userserviceurl.concat("/user/" + id);
        //RestTemplate restTemplate = new RestTemplate();
        //Long rankdiscount = restTemplate.getForObject(path, Long.class);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", authorization);
        try{
            UserDto userDto = restTemplate.exchange(path, HttpMethod.GET, new HttpEntity<>(headers), UserDto.class).getBody();
            return userDto;
        }
        catch (HttpClientErrorException e){
            e.printStackTrace();
        }
        return null;
    }
    public ActiveReservationDto reserveVehicle(ActiveReservationCreateDto newreservation, String authorization)
    {
        Claims claims = normalTokenService.parseToken(authorization);
        CompanyCar companyCar = companyCarRepository.findById(newreservation.getCompanycarid()).orElse(null);
        if(companyCar == null)
        {
            throw new CarNotFoundException("Car with the id " + newreservation.getCompanycarid() + " not found.");
        }
        //List<ActiveReservation> rentlist = activeReservationRepository.findAllActiveReservationByCompanyCar(companyCar);
        int avaliable = companyCar.getNumberofcars();
        List<ActiveReservation> rentlist = activeReservationRepository.findAllActiveReservationByCompanyCar(companyCar);
        for(ActiveReservation rent: rentlist)
        {
            if(rent.getEnddate().isAfter(newreservation.getBegindate()))
            {
                avaliable--;
                if(avaliable == 0) {
                    throw new CarNotAvaliableException("Car with the id " + newreservation.getCompanycarid() + " is not avaliable for the given timeframe");
                }
            }
            else if(newreservation.getEnddate().isBefore(rent.getBegindate()))
            {
                avaliable--;
                if(avaliable == 0) {
                    throw new CarNotAvaliableException("Car with the id " + newreservation.getCompanycarid() + " is not avaliable for the given timeframe");
                }
            }
        }

        Long rankdiscount = Retry.decorateSupplier(userServiceRetry, () -> getRankDiscount(claims, authorization)).get();

        ActiveReservation activeReservation = ActiveReservationMapper.activeReservationCreateDtoToActiveReservation(newreservation);
        activeReservation.setClientId(claims.get("id", Long.class));
        Long totalprice = Duration.between(activeReservation.getBegindate().atTime(0,0), activeReservation.getEnddate().atTime(0,0)).toDays() * companyCar.getPrice() / rankdiscount;
        activeReservation.setTotalprice(totalprice);
        activeReservationRepository.save(activeReservation);

        SuccessfulReservationClientNotification notification;
        UserDto clientDto = Retry.decorateSupplier(userServiceRetry, () -> getUserDto(claims.get("id", Long.class), authorization)).get();
        notification = NotificationMapper.activeReservationToClientNotification(activeReservation, clientDto);
        emailService.sendMessage(notification, "reservation");

        SuccessfulReservationManagerNotification managerNotification;
        UserDto managerDto = Retry.decorateSupplier(userServiceRetry, () -> getUserDto(companyCar.getCompany().getId(), authorization)).get();
        managerNotification = NotificationMapper.activeReservationToManagerNotification(activeReservation, managerDto);
        emailService.sendMessage(managerNotification, "reservationmanager");

        return ActiveReservationMapper.activeReservationToActiveReservationDto(activeReservation);
    }

    @Override
    public ReviewDto leaveAReview(String authorization, ReviewDto reviewDto) {
        Long rentalId = reviewDto.getId();
        Claims claims = normalTokenService.parseToken(authorization);
        //TODO: SECURITY CHECK!!!!
        //CompletedRental completedRental = completedRentalRepository.getReferenceById(rentalId);
        CompletedRental completedRental = completedRentalRepository.findById(rentalId).orElseThrow(()->new NotFoundException("Rental with id " + rentalId +" not found!"));
        Long userId = claims.get("id", Long.class);
        if(completedRental.getClientId()!=userId){
            throw new UnauthorizedException("You can't leave a review for someone else's rental experience!");
        }
        if(reviewDto.getStars() < 1 || reviewDto.getStars() > 5){
            throw new BadStarsException("Rating has to be between 1 and 5 stars!");
        }
        completedRental.setComment(reviewDto.getComment());
        completedRental.setRating(reviewDto.getStars());
        //TODO: mozda da imamo neki counter za recenzije za kompanije, kako bi ubrzali servis kasnije?
        completedRentalRepository.save(completedRental);
        return reviewDto;
    }

    private Long getCompanyId(String path, String authorization){

        //RestTemplate restTemplate = new RestTemplate();
        //Long rankdiscount = restTemplate.getForObject(path, Long.class);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", authorization);
        try{
            Long companyId = restTemplate.exchange(path, HttpMethod.GET, new HttpEntity<>(headers), Long.class).getBody();
            return companyId;
        }
        catch (HttpClientErrorException e){
            e.printStackTrace();
        }
        return null;
    }

    private UserDto getUserDto(String path, String authorization){
        //RestTemplate restTemplate = new RestTemplate();
        //Long rankdiscount = restTemplate.getForObject(path, Long.class);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", authorization);
        try{
            UserDto userDto = restTemplate.exchange(path, HttpMethod.GET, new HttpEntity<>(headers), UserDto.class).getBody();
            return userDto;
        }
        catch (HttpClientErrorException e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void cancelReservation(Long rentalId, String authorization) {
        Claims claims =normalTokenService.parseToken(authorization);
        Long userId = claims.get("id", Long.class);
        String role = claims.get("role", String.class);
        ActiveReservation activeReservation = activeReservationRepository.findById(rentalId).orElseThrow(()->new NotFoundException("Reservation with id " + rentalId +" not found!"));
        if(role.equals("ROLE_CLIENT") && !Objects.equals(activeReservation.getClientId(), userId)){
            throw new UnauthorizedException("Cannot cancel reservation that isn't yours!");
        }
        //TODO: za menadzera moram da pozovem userservice da vidim u kojoj firmi menadzer radi

        if(role.equals("ROLE_MANAGER")){

            /*
            String path = userserviceurl.concat("/user/manager/" + userId);
            RestTemplate restTemplate = new RestTemplate();
            Long companyId = restTemplate.getForObject(path, Long.class);

             */
            String path = userserviceurl.concat("/user/manager/" + userId);
            Long companyId = getCompanyId(path, authorization);

            if(!Objects.equals(activeReservation.getCompanyCar().getCompany().getId(), companyId)){
                throw new UnauthorizedException("Cannot cancel reservation that isn't from your company!");
            }
        }
        Long clientId = activeReservation.getClientId();
        Long managerId = activeReservation.getCompanyCar().getCompany().getManagerid();

        activeReservationRepository.delete(activeReservation);

        String path = userserviceurl.concat("/user/" + clientId);
        //RestTemplate restTemplate = new RestTemplate();
        UserDto client = Retry.decorateSupplier(userServiceRetry,()->  getUserDto(path, authorization)).get();
        String path1 = userserviceurl.concat("/user/" + managerId);
        UserDto manager = Retry.decorateSupplier(userServiceRetry,()->  getUserDto(path1, authorization)).get();
        //TODO: notify servis ovde
        CancelReservationClientNotification clientmessage = NotificationMapper.activeReservationToCancelReservationClientNotification(activeReservation, client);
        CancelReservationManagerNotification managermessage = NotificationMapper.activeReservationToCancelReservationManagerNotification(activeReservation, manager);
        //sendMessage(clientmessage, "cancelreservationclient");
        //sendMessage(managermessage, "cancelreservationmanager");
        emailService.sendMessage(clientmessage, "cancelreservationclient");
        emailService.sendMessage(managermessage, "cancelreservationmanager");
    }

    @Override
    public List<ActiveReservationDto> getMyCurrentReservations(String authorization) {
        Claims claims = normalTokenService.parseToken(authorization);
        if(claims.get("role", String.class).equals("ROLE_CLIENT")){
            Long id = claims.get("id", Long.class);
            return activeReservationRepository.findAll().stream().filter(activeReservation -> activeReservation.getClientId().equals(id))
                    .map(ActiveReservationMapper::activeReservationToActiveReservationDto).collect(Collectors.toList());
        }
        Company company = companyRepository.findCompanyByManagerid(claims.get("id", Long.class)).orElseThrow(()->new NotFoundException("Company not found!"));
        return activeReservationRepository.findAll().stream().filter(activeReservation -> activeReservation.getCompanyCar().getCompany().getId().equals(company.getId()))
                .map(ActiveReservationMapper::activeReservationToActiveReservationDto).collect(Collectors.toList());
    }

    @Override
    public List<CompletedRentalDto> getMyCompletedReservations(String authorization) {
        Claims claims = normalTokenService.parseToken(authorization);
        Long id = claims.get("id", Long.class);
        return completedRentalRepository.findAll().stream().filter(completedRental -> completedRental.getClientId().equals(id))
                .map(CompletedRentalMapper::completedRentalToCompletedRentalDto).collect(Collectors.toList());
    }
}
