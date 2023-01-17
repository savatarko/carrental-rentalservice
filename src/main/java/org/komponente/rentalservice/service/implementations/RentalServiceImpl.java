package org.komponente.rentalservice.service.implementations;

import io.jsonwebtoken.Claims;
import lombok.AllArgsConstructor;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQQueue;
import org.komponente.dto.carrental.CompanyCarDto;
import org.komponente.dto.client.ClientDto;
import org.komponente.dto.email.CancelReservationClientNotification;
import org.komponente.dto.email.CancelReservationManagerNotification;
import org.komponente.dto.requests.CarSearchFilterDto;
import org.komponente.dto.reservation.ActiveReservationCreateDto;
import org.komponente.dto.reservation.ActiveReservationDto;
import org.komponente.dto.review.ReviewDto;
import org.komponente.dto.user.UserDto;
import org.komponente.dto.vehicle.VehicleDto;
import org.komponente.rentalservice.domain.*;
import org.komponente.rentalservice.exceptions.*;
import org.komponente.rentalservice.mapper.ActiveReservationMapper;
import org.komponente.rentalservice.mapper.MessageMapper;
import org.komponente.rentalservice.mapper.VehicleMapper;
import org.komponente.rentalservice.repository.*;
import org.komponente.rentalservice.security.token.TokenService;
import org.komponente.rentalservice.service.CompanyService;
import org.komponente.rentalservice.service.RentalService;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.jms.*;
import java.io.Serializable;
import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class RentalServiceImpl implements RentalService {
    private VehicleRepository vehicleRepository;
    private CompanyRepository companyRepository;
    private ActiveReservationRepository activeReservationRepository;
    private CompletedRentalRepository completedRentalRepository;
    private CompanyCarRepository companyCarRepository;

    //private TokenService tokenService;

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
    public List<VehicleDto> searchVehicles(CarSearchFilterDto carSearchFilterDto) {
        List<VehicleDto> output= new ArrayList<>();
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
            output.add(VehicleMapper.vehicleToVehicleDto(v.getVehicle()));
        }
        return output;
    }

    public ActiveReservationDto reserveVehicle(ActiveReservationCreateDto newreservation, Long clientid)
    {
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
        String path = userserviceurl.concat("/rank/" + clientid);
        RestTemplate restTemplate = new RestTemplate();
        Long rankdiscount = restTemplate.getForObject(path, Long.class);

        ActiveReservation activeReservation = ActiveReservationMapper.activeReservationCreateDtoToActiveReservation(newreservation);
        Long totalprice = Duration.between(activeReservation.getBegindate(), activeReservation.getEnddate()).toDays() * companyCar.getPrice() / rankdiscount;
        activeReservation.setTotalprice(totalprice);
        activeReservationRepository.save(activeReservation);
        return ActiveReservationMapper.activeReservationToActiveReservationDto(activeReservation);
    }

    @Override
    public ReviewDto leaveAReview(Long rentalId,Long userId, ReviewDto reviewDto) {
        //TODO: SECURITY CHECK!!!!
        //CompletedRental completedRental = completedRentalRepository.getReferenceById(rentalId);
        CompletedRental completedRental = completedRentalRepository.findById(rentalId).orElseThrow(()->new NotFoundException("Rental with id " + rentalId +" not found!"));
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

    @Override
    public void cancelReservation(Long rentalId, Claims claims) {
        Long userId = claims.get("id", Long.class);
        String role = claims.get("role", String.class);
        ActiveReservation activeReservation = activeReservationRepository.findById(rentalId).orElseThrow(()->new NotFoundException("Reservation with id " + rentalId +" not found!"));
        if(role.equals("ROLE_USER") && !Objects.equals(activeReservation.getClientId(), userId)){
            throw new UnauthorizedException("Cannot cancel reservation that isn't yours!");
        }
        //TODO: za menadzera moram da pozovem userservice da vidim u kojoj firmi menadzer radi

        if(role.equals("ROLE_MANAGER")){

            String path = userserviceurl.concat("/user/manager/" + userId);
            RestTemplate restTemplate = new RestTemplate();
            Long companyId = restTemplate.getForObject(path, Long.class);

            if(!Objects.equals(activeReservation.getCompanyCar().getCompany().getId(), companyId)){
                throw new UnauthorizedException("Cannot cancel reservation that isn't from your company!");
            }
        }
        Long clientId = activeReservation.getClientId();
        Long managerId = activeReservation.getCompanyCar().getCompany().getManagerid();

        activeReservationRepository.delete(activeReservation);

        String path = userserviceurl.concat("/user/" + clientId);
        RestTemplate restTemplate = new RestTemplate();
        UserDto client = restTemplate.getForObject(path, UserDto.class);
        path = userserviceurl.concat("/user/" + managerId);
        UserDto manager = restTemplate.getForObject(path, UserDto.class);
        //TODO: notify servis ovde
        CancelReservationClientNotification clientmessage = MessageMapper.cancelReservationClientNotificationBuilder(client);
        CancelReservationManagerNotification managermessage = MessageMapper.cancelReservationManagerNotificationBuilder(manager);
        sendMessage(clientmessage, "cancelreservationclient");
        sendMessage(managermessage, "cancelreservationmanager");
    }
}
