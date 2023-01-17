package org.komponente.rentalservice.mapper;

import org.komponente.dto.email.CancelReservationClientNotification;
import org.komponente.dto.email.CancelReservationManagerNotification;
import org.komponente.dto.user.UserDto;

public class MessageMapper {
    public static CancelReservationClientNotification cancelReservationClientNotificationBuilder(UserDto userDto){
        CancelReservationClientNotification cancelReservationClientNotification = new CancelReservationClientNotification();
        cancelReservationClientNotification.setEmail(userDto.getEmail());
        cancelReservationClientNotification.setUsername(userDto.getUsername());
        return cancelReservationClientNotification;
    }

    public static CancelReservationManagerNotification cancelReservationManagerNotificationBuilder(UserDto userDto){
        CancelReservationManagerNotification cancelReservationManagerNotification = new CancelReservationManagerNotification();
        cancelReservationManagerNotification.setEmail(userDto.getEmail());
        return cancelReservationManagerNotification;
    }
}
