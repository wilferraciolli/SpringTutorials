package com.wiltech.insurly.account.identity;

/**
 * Thrown when an {@code /api/users/**} or {@code /api/admin/**} endpoint is
 * hit without a resolvable identity. Mapped to HTTP 401 by
 * {@code GlobalExceptionHandler}.
 */
public class UnauthenticatedException extends RuntimeException {
    public UnauthenticatedException(final String message) {
        super(message);
    }
}
