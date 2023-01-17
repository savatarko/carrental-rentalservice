package org.komponente.rentalservice.repository;

import org.komponente.rentalservice.domain.Company;
import org.komponente.rentalservice.domain.VehicleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VehicleTypeRepository extends JpaRepository<VehicleType, Long> {

    Optional<VehicleType> findVehicleTypeByType(String type);
}
