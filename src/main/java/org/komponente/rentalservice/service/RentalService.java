package org.komponente.rentalservice.service;

import io.jsonwebtoken.Claims;
import org.komponente.dto.carrental.CompanyCarDto;
import org.komponente.dto.client.ClientDto;
import org.komponente.dto.requests.CarSearchFilterDto;
import org.komponente.dto.reservation.ActiveReservationCreateDto;
import org.komponente.dto.reservation.ActiveReservationDto;
import org.komponente.dto.review.ReviewDto;
import org.komponente.dto.vehicle.VehicleDto;

import java.util.List;

public interface RentalService {
    List<VehicleDto> searchVehicles(CarSearchFilterDto carSearchFilterDto);

    ActiveReservationDto reserveVehicle(ActiveReservationCreateDto newreservation, Long clientId);
    ReviewDto leaveAReview(Long rentalId, Long userId, ReviewDto reviewDto);
    void cancelReservation(Long rentalId, Claims claims);
}
