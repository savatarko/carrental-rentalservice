package org.komponente.rentalservice.repository;

import org.komponente.rentalservice.domain.Company;
import org.komponente.rentalservice.domain.CompanyCar;
import org.komponente.rentalservice.domain.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CompanyCarRepository extends JpaRepository<CompanyCar, Long> {
    Optional<CompanyCar> findCompanyCarByCompanyAndVehicle(Company company, Vehicle vehicle);
}
