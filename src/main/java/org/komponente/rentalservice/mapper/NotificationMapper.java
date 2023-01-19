package org.komponente.rentalservice.mapper;

import org.komponente.dto.client.ClientDto;
import org.komponente.dto.email.*;
import org.komponente.dto.manager.ManagerDto;
import org.komponente.dto.user.UserDto;
import org.komponente.rentalservice.domain.ActiveReservation;

public class NotificationMapper {
    public static SuccessfulReservationClientNotification activeReservationToClientNotification(ActiveReservation activeReservation, UserDto clientDto){

        SuccessfulReservationClientNotification notification = new SuccessfulReservationClientNotification();
        notification.setEmail(clientDto.getEmail());
        notification.setUsername(clientDto.getUsername());
        notification.setCompanyname(activeReservation.getCompanyCar().getCompany().getName());
        notification.setVehiclename(activeReservation.getCompanyCar().getVehicle().getName());
        notification.setStartdate(activeReservation.getBegindate().toString());
        notification.setEnddate(activeReservation.getEnddate().toString());
        notification.setTotalprice(activeReservation.getTotalprice());
        notification.setReceiverId(clientDto.getId());
        return notification;
    }

    public static SuccessfulReservationManagerNotification activeReservationToManagerNotification(ActiveReservation activeReservation, UserDto managerDto){
        SuccessfulReservationManagerNotification notification = new SuccessfulReservationManagerNotification();
        notification.setEmail(managerDto.getEmail());
        notification.setCompanyname(activeReservation.getCompanyCar().getCompany().getName());
        notification.setVehiclename(activeReservation.getCompanyCar().getVehicle().getName());
        notification.setStartdate(activeReservation.getBegindate().toString());
        notification.setEnddate(activeReservation.getEnddate().toString());
        notification.setReceiverId(managerDto.getId());
        return notification;
    }

    public static ReservationReminderNotification activeReservationToReservationReminderNotification(ActiveReservation activeReservation, UserDto clientDto){
        ReservationReminderNotification notification = new ReservationReminderNotification();
        notification.setEmail(clientDto.getEmail());
        notification.setUsername(clientDto.getUsername());
        notification.setCompanyname(activeReservation.getCompanyCar().getCompany().getName());
        notification.setVehiclename(activeReservation.getCompanyCar().getVehicle().getName());
        notification.setStartdate(activeReservation.getBegindate().toString());
        notification.setEnddate(activeReservation.getEnddate().toString());
        notification.setReceiverId(clientDto.getId());
        return notification;
    }

    public static CancelReservationClientNotification activeReservationToCancelReservationClientNotification(ActiveReservation activeReservation, UserDto clientDto){
        CancelReservationClientNotification notification = new CancelReservationClientNotification();
        notification.setEmail(clientDto.getEmail());
        notification.setUsername(clientDto.getUsername());
        notification.setCompanyname(activeReservation.getCompanyCar().getCompany().getName());
        notification.setVehiclename(activeReservation.getCompanyCar().getVehicle().getName());
        notification.setStartdate(activeReservation.getBegindate().toString());
        notification.setEnddate(activeReservation.getEnddate().toString());
        notification.setReceiverId(clientDto.getId());
        return notification;
    }

    public static CancelReservationManagerNotification activeReservationToCancelReservationManagerNotification(ActiveReservation activeReservation, UserDto managerDto){
        CancelReservationManagerNotification notification = new CancelReservationManagerNotification();
        notification.setEmail(managerDto.getEmail());
        notification.setCompanyname(activeReservation.getCompanyCar().getCompany().getName());
        notification.setVehiclename(activeReservation.getCompanyCar().getVehicle().getName());
        notification.setStartdate(activeReservation.getBegindate().toString());
        notification.setEnddate(activeReservation.getEnddate().toString());
        notification.setReceiverId(managerDto.getId());
        return notification;
    }
}
