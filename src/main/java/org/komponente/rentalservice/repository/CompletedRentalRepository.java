package org.komponente.rentalservice.repository;

import org.komponente.rentalservice.domain.CompletedRental;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompletedRentalRepository extends JpaRepository<CompletedRental, Long> {
}
