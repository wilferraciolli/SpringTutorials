package com.wiltech.insurly.validations;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.DateTimeException;
import java.time.ZoneId;

public class ValidZoneIdValidator implements ConstraintValidator<ValidZoneId, String> {

    @Override
    public boolean isValid(final String value, final ConstraintValidatorContext context) {
        if (value == null || value.isBlank()) {
            return true; // let @NotBlank / @NotNull own the empty case
        }
        try {
            ZoneId.of(value);
            return true;
        } catch (final DateTimeException ex) {
            return false;
        }
    }
}
