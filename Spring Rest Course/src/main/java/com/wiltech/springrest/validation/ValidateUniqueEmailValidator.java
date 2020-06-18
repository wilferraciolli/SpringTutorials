package com.wiltech.springrest.validation;

import java.util.Objects;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.wiltech.springrest.clients.Client;
import com.wiltech.springrest.clients.ClientRepository;

@Component
public class ValidateUniqueEmailValidator implements ConstraintValidator<ValidateUniqueEmail, String> {

    @Autowired
    private ClientRepository clientRepository;

    private boolean allowBlanks;

    @Override
    public void initialize(ValidateUniqueEmail constraintAnnotation) {
        allowBlanks = constraintAnnotation.allowBlanks();
    }

    @Override
    public boolean isValid(final String value, final ConstraintValidatorContext context) {
        boolean result = false;

        // check if blank or empty
        if (StringUtils.isBlank(value)) {

            return allowBlanks;
        } else {

            return !clientRepository.findByEmail(value).isPresent();

        }
    }
}
