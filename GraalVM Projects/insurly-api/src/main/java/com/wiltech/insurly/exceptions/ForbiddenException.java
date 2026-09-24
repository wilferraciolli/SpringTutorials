package com.wiltech.insurly.exceptions;

/**
 * Thrown when the caller is authenticated but not allowed to perform the action
 * (e.g. a non-admin hitting {@code /api/admin/**}, or an admin trying to write
 * to a self-service user's data). Mapped to HTTP 403 by {@link GlobalExceptionHandler}.
 */
public class ForbiddenException extends RuntimeException {
    public ForbiddenException(final String message) {
        super(message);
    }
}
