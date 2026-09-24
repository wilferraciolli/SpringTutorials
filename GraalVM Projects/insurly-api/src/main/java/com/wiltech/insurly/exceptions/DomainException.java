package com.wiltech.insurly.exceptions;

/**
 * Thrown by an {@code *ApplicationService} when a requested resource brakes domain logic.
 * Mapped to HTTP 404 by {@link GlobalExceptionHandler}.
 */
public class DomainException extends RuntimeException {
    public DomainException(String message) {
        super(message);
    }
}
