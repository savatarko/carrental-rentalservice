package org.komponente.rentalservice.service.scheduled;

import lombok.AllArgsConstructor;
import org.komponente.dto.email.ReservationReminderNotification;
import org.komponente.dto.user.UserDto;
import org.komponente.rentalservice.mapper.CompletedRentalMapper;
import org.komponente.rentalservice.repository.ActiveReservationRepository;
import org.komponente.rentalservice.repository.CompletedRentalRepository;
import org.komponente.rentalservice.service.EmailService;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.List;

@AllArgsConstructor
@Configuration
@EnableScheduling
public class CompletedRentalCheck {

    private ActiveReservationRepository  activeReservationRepository;
    private CompletedRentalRepository completedRentalRepository;
    private EmailService emailService;

    @Scheduled(initialDelay = 10000, fixedDelay = 86400000)
    public void checkCompletedRentals() {
        System.out.println("Checking completed rentals...");
        activeReservationRepository.findAll().forEach(activeReservation -> {
            if (activeReservation.getEnddate().isBefore(java.time.LocalDate.now())) {
                completedRentalRepository.save(CompletedRentalMapper.activeReservationToCompletedRental(activeReservation));
                activeReservationRepository.delete(activeReservation);
            }
        });
    }

    private UserDto getUserDto(String path, String authorization){
        RestTemplate restTemplate = new RestTemplate();
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

    @Scheduled(initialDelay = 10000, fixedDelay = 86400000)
    public void checkRentalNotification() {
        System.out.println("Checking for 3 day notifications...");
        activeReservationRepository.findAll().forEach(activeReservation -> {
            if (Duration.between(java.time.LocalDate.now().atStartOfDay(), activeReservation.getBegindate().atStartOfDay()).toDays() == 3) {
                ReservationReminderNotification notification;
                //UserDto userDto = getUserDto("http://localhost:8081/user/" + activeReservation.getClientId(), activeReservation.getToken());
                //TODO: kako uraditi ovo sigurno?
            }
        });
    }
}
