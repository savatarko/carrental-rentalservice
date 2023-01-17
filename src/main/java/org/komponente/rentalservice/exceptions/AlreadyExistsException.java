package org.komponente.rentalservice.exceptions;

import org.springframework.http.HttpStatus;

public class AlreadyExistsException extends CustomException{

    public AlreadyExistsException(String message) {
        super(message, ErrorCode.BAD_REQUEST, HttpStatus.BAD_REQUEST);
    }
}
