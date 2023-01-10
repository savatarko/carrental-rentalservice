package org.komponente.rentalservice.exceptions;

import org.springframework.http.HttpStatus;

public class CarNotAvaliableException extends CustomException{
    public CarNotAvaliableException(String message) {
        super(message, ErrorCode.CAR_NOT_AVALIABLE, HttpStatus.BAD_REQUEST);
    }
}
