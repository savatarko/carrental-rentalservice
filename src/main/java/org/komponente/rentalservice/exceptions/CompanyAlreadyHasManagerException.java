package org.komponente.rentalservice.exceptions;

import org.springframework.http.HttpStatus;

public class CompanyAlreadyHasManagerException extends CustomException{
    public CompanyAlreadyHasManagerException(String message) {
        super(message, ErrorCode.COMPANY_HAS_MANAGER, HttpStatus.BAD_REQUEST);
    }
}
