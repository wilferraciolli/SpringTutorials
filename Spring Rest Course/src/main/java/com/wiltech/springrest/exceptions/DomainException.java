package com.wiltech.springrest.exceptions;

/**
 * Class to be used as exception of Domain Logic Exception.
 */
public class DomainException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public DomainException(String message) {
        super(message);
    }
}
