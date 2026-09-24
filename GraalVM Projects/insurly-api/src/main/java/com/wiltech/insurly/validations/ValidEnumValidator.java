package com.wiltech.insurly.validations;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.ArrayList;
import java.util.List;

public class ValidEnumValidator implements ConstraintValidator<ValidEnum, String> {
    /**
     * The Value list.
     */
    List<String> valueList = new ArrayList<>();

    @Override
    public void initialize(final ValidEnum constraintAnnotation) {
        this.valueList = new ArrayList<String>();
        final Class<? extends Enum<?>> enumClass = constraintAnnotation.enumClazz();

        @SuppressWarnings("rawtypes")
        final Enum[] enumValArr = enumClass.getEnumConstants();

        for (@SuppressWarnings("rawtypes") final Enum enumVal : enumValArr) {
            this.valueList.add(enumVal.toString().toUpperCase());
        }
    }

    @Override
    public boolean isValid(final String value, final ConstraintValidatorContext context) {
        return this.valueList.contains(value.toUpperCase());
    }
}
