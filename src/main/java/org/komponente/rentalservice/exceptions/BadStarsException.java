package org.komponente.rentalservice.exceptions;

import org.springframework.http.HttpStatus;

public class BadStarsException extends CustomException{
    public BadStarsException(String message) {
        super(message, ErrorCode.BAD_STARS, HttpStatus.BAD_REQUEST);
    }
}
