package com.mautz.ticketrush.ticketrush.shared.exception;

public class UnauthorizedException extends DomainException {

    public UnauthorizedException(String message) {
        super(message);
    }
}
