package org.komponente.rentalservice.repository;

import org.komponente.rentalservice.domain.CompanyCar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompanyCarRepository extends JpaRepository<CompanyCar, Long> {
}
