package org.komponente.rentalservice.domain;

//import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
public class CompanyCar {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    private Company company;

    @ManyToOne
    private Vehicle vehicle;

    private Long price;
    private Integer numberofcars;
}
