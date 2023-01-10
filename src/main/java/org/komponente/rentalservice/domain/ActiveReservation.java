package org.komponente.rentalservice.domain;

//import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;
import org.komponente.dto.reservation.ActiveReservationDto;
import org.komponente.rentalservice.mapper.CompanyCarMapper;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Getter
@Setter
@Entity
public class ActiveReservation extends Reservation{

}
