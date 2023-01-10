package org.komponente.rentalservice.exceptions;

import org.springframework.http.HttpStatus;

public class CompanyAlreadyExistsException extends CustomException{

    public CompanyAlreadyExistsException(String message) {
        super(message, ErrorCode.BAD_REQUEST, HttpStatus.BAD_REQUEST);
    }
}
