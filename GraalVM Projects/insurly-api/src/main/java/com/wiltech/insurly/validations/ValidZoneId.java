package com.wiltech.insurly.validations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Passes when the annotated string is a recognised {@link java.time.ZoneId}
 * (IANA name or fixed offset). A null / blank value passes — pair it with
 * {@code @NotBlank} to require one.
 */
@Documented
@Constraint(validatedBy = ValidZoneIdValidator.class)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ValidZoneId {

    String message() default "Not a recognised timezone";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
