package org.komponente.rentalservice.domain;

//import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Getter
@Setter
@Entity
public class CompletedRental extends Reservation{
    private Integer rating;
    private String comment;

}
