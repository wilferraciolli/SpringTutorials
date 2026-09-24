package com.wiltech.insurly.exceptions;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

import org.springframework.http.HttpStatus;

import static java.lang.String.valueOf;

/**
 * Single JSON error shape returned for every handled exception. Produced only by
 * {@link GlobalExceptionHandler} — controllers never build their own error bodies.
 */
public record ApiError(
        Instant timestamp,
        int status,
        String error,
        String message,
        String path,
        List<FieldViolation> fieldErrors) {

    public record FieldViolation(
            String field,
            Object fieldValue,
            String message) {

        // helper method to always get a string representation of the violation field value
        @Override
        public Object fieldValue() {
            return Objects.nonNull(fieldValue)
                    ? valueOf(fieldValue)
                    : "";
        }
    }

    public static ApiError of(HttpStatus status, String message, String path) {
        return new ApiError(Instant.now(), status.value(), status.getReasonPhrase(), message, path, List.of());
    }

    public static ApiError of(HttpStatus status, String message, String path, List<FieldViolation> fieldErrors) {
        return new ApiError(Instant.now(), status.value(), status.getReasonPhrase(), message, path, fieldErrors);
    }
}
