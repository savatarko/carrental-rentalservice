package org.komponente.rentalservice.domain;

//import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

import java.time.LocalDate;
import java.util.Date;
@Getter
@Setter
@MappedSuperclass
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    private CompanyCar companyCar;

    private Long clientId;

    //private Long managerid;

    //@Temporal(TemporalType.DATE)
    private LocalDate begindate;
    //@Temporal(TemporalType.DATE)
    private LocalDate enddate;

    private Long totalprice;
}
