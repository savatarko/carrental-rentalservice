package org.komponente.rentalservice.service.scheduled;

import lombok.AllArgsConstructor;
import org.komponente.rentalservice.mapper.CompletedRentalMapper;
import org.komponente.rentalservice.repository.ActiveReservationRepository;
import org.komponente.rentalservice.repository.CompletedRentalRepository;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@AllArgsConstructor
@Configuration
@EnableScheduling
public class CompletedRentalCheck {

    private ActiveReservationRepository  activeReservationRepository;
    private CompletedRentalRepository completedRentalRepository;

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
}
