package org.komponente.rentalservice.service;

import io.jsonwebtoken.Claims;
import org.komponente.dto.carrental.CompanyCarDto;
import org.komponente.dto.client.ClientDto;
import org.komponente.dto.completedrental.CompletedRentalDto;
import org.komponente.dto.requests.CarSearchFilterDto;
import org.komponente.dto.reservation.ActiveReservationCreateDto;
import org.komponente.dto.reservation.ActiveReservationDto;
import org.komponente.dto.review.ReviewDto;
import org.komponente.dto.vehicle.VehicleDto;
import org.komponente.rentalservice.domain.CompletedRental;
import org.springframework.data.domain.Page;

import java.util.List;

public interface RentalService {
    List<CompanyCarDto> searchVehicles(CarSearchFilterDto carSearchFilterDto);

    ActiveReservationDto reserveVehicle(ActiveReservationCreateDto newreservation, String authorization);
    ReviewDto leaveAReview(String authorization, ReviewDto reviewDto);
    void cancelReservation(Long rentalId, String authorization);

    List<ActiveReservationDto> getMyCurrentReservations(String authorization);
    List<CompletedRentalDto> getMyCompletedReservations(String authorization);
}
