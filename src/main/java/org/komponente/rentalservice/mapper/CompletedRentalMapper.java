package org.komponente.rentalservice.mapper;

import org.komponente.dto.completedrental.CompletedRentalDto;
import org.komponente.dto.reservation.ActiveReservationDto;
import org.komponente.rentalservice.domain.ActiveReservation;
import org.komponente.rentalservice.domain.CompletedRental;
import org.springframework.stereotype.Component;

@Component
public class CompletedRentalMapper {
    public static CompletedRental activeReservationToCompletedRental(ActiveReservation activeReservation){
        CompletedRental completedRental = new CompletedRental();
        completedRental.setTotalprice(activeReservation.getTotalprice());
        completedRental.setBegindate(activeReservation.getBegindate());
        completedRental.setEnddate(activeReservation.getEnddate());
        completedRental.setCompanyCar(activeReservation.getCompanyCar());
        completedRental.setClientId(activeReservation.getClientId());
        return completedRental;
    }

    public static CompletedRentalDto completedRentalToCompletedRentalDto(CompletedRental completedRental){
        CompletedRentalDto completedRentalDto = new CompletedRentalDto();
        completedRentalDto.setBegindate(completedRental.getBegindate());
        completedRentalDto.setEnddate(completedRental.getEnddate());
        completedRentalDto.setClientId(completedRental.getClientId());
        completedRentalDto.setId(completedRental.getId());
        completedRentalDto.setStars(completedRentalDto.getStars());
        completedRentalDto.setComment(completedRental.getComment());
        completedRentalDto.setTotalprice(completedRental.getTotalprice());
        completedRentalDto.setCompanyCarDto(CompanyCarMapper.companyCarToCompanyCarDto(completedRental.getCompanyCar()));
        return completedRentalDto;
    }
}
