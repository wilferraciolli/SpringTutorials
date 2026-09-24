package com.wiltech.insurly.exceptions;

/**
 * Thrown by an {@code *ApplicationService} when a requested resource does not exist.
 * Mapped to HTTP 404 by {@link GlobalExceptionHandler}.
 */
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
