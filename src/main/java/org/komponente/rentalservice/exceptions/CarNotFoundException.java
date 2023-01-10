package org.komponente.rentalservice.exceptions;

import org.springframework.http.HttpStatus;

public class CarNotFoundException extends CustomException{
    public CarNotFoundException(String message) {
        super(message, ErrorCode.CAR_NOT_FOUND, HttpStatus.BAD_REQUEST);
    }
}
