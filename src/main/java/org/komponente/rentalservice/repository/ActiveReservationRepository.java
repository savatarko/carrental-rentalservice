package org.komponente.rentalservice.repository;

import org.komponente.rentalservice.domain.ActiveReservation;
import org.komponente.rentalservice.domain.CompanyCar;
import org.komponente.rentalservice.domain.Reservation;
import org.komponente.rentalservice.domain.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ActiveReservationRepository extends JpaRepository<ActiveReservation, Long> {
    //List<ActiveReservation> findAllActiveReservationByVehicle(Vehicle vehicle);
    List<ActiveReservation> findAllActiveReservationByCompanyCar(CompanyCar companyCar);
}
