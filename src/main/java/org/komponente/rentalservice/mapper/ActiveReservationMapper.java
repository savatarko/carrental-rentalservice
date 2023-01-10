package org.komponente.rentalservice.mapper;

import org.komponente.dto.reservation.ActiveReservationCreateDto;
import org.komponente.dto.reservation.ActiveReservationDto;
import org.komponente.rentalservice.domain.ActiveReservation;
import org.komponente.rentalservice.exceptions.NotFoundException;
import org.komponente.rentalservice.repository.CompanyCarRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ActiveReservationMapper {
    private static CompanyCarRepository companyCarRepository;

    @Autowired
    public void initRepo(CompanyCarRepository companyCarRepository){
        ActiveReservationMapper.companyCarRepository = companyCarRepository;
    }

    public static ActiveReservationDto activeReservationToActiveReservationDto(ActiveReservation activeReservation){
        ActiveReservationDto activeReservationDto = new ActiveReservationDto();
        activeReservationDto.setBegindate(activeReservationDto.getBegindate());
        activeReservationDto.setEnddate(activeReservation.getEnddate());
        activeReservationDto.setId(activeReservationDto.getId());
        activeReservationDto.setTotalprice(activeReservationDto.getTotalprice());
        activeReservationDto.setClientId(activeReservationDto.getClientId());
        activeReservationDto.setCompanyCarDto(CompanyCarMapper.companyCarToCompanyCarDto(activeReservation.getCompanyCar()));
        return activeReservationDto;
    }
    public static ActiveReservation activeReservationCreateDtoToActiveReservation(ActiveReservationCreateDto activeReservationCreateDto)
    {
        ActiveReservation activeReservation = new ActiveReservation();
        activeReservation.setTotalprice(activeReservationCreateDto.getTotalprice());
        activeReservation.setBegindate(activeReservationCreateDto.getBegindate());
        activeReservation.setEnddate(activeReservationCreateDto.getEnddate());
        activeReservation.setClientId(activeReservation.getClientId());
        activeReservation.setCompanyCar(companyCarRepository.findById(activeReservationCreateDto.getCompanycarid()).orElseThrow(() -> new NotFoundException(String
                .format("Car owned by a company with id: %d does not exists.", activeReservationCreateDto.getCompanycarid()))));

        return activeReservation;
    }
}
