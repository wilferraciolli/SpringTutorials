package com.wiltech.insurly.validations;



import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.ReportAsSingleViolation;
import jakarta.validation.constraints.NotNull;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Constraint(validatedBy = ValidEnumValidator.class)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@NotNull(message = "Value cannot be null")
@ReportAsSingleViolation
public @interface ValidEnum {
    /**
     * Enum clazz class.
     * @return the class
     */
    Class<? extends Enum<?>> enumClazz();

    /**
     * Message string.
     * @return the string
     */
    String message() default "Enum Value is not valid";

    /**
     * Groups class [ ].
     * @return the class [ ]
     */
    Class<?>[] groups() default {};

    /**
     * Payload class [ ].
     * @return the class [ ]
     */
    Class<? extends Payload>[] payload() default {};

}
