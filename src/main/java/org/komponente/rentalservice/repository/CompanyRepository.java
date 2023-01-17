package org.komponente.rentalservice.repository;

import org.komponente.rentalservice.domain.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {

    Company findCompanyByName(String name);
    Optional<Company> findCompanyByManagerid(Long managerid);
}
