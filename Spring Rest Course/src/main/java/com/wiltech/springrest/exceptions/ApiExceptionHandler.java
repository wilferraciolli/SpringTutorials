package com.wiltech.springrest.exceptions;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {

    @Autowired
    private MessageSource messageSource;

    @ExceptionHandler(DomainException.class)
    public ResponseEntity<Object> handleDomainException(DomainException ex, WebRequest request) {
        var status = HttpStatus.BAD_REQUEST;

        var error = Error.builder()
                .statusCode(status.value())
                .title(ex.getMessage())
                .dateTime(LocalDateTime.now())
                .build();

        return handleExceptionInternal(ex, error, new HttpHeaders(), status, request);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers,
            HttpStatus status, WebRequest request) {

        var error = Error.builder()
                .statusCode(status.value())
                .title("Error")
                .dateTime(LocalDateTime.now())
                .fields(getFailedValidationFields(ex))
                .build();

        return super.handleExceptionInternal(ex, error, headers, status, request);
    }

    private List<PropertyField> getFailedValidationFields(MethodArgumentNotValidException ex) {
        return ex.getBindingResult().getAllErrors()
                .stream()
                .map(this::buildPropertyFieldError)
                .collect(Collectors.toList());
    }

    private PropertyField buildPropertyFieldError(ObjectError error) {

        return new PropertyField(((FieldError) error).getField(),((FieldError) error).getRejectedValue(),  messageSource.getMessage(error, LocaleContextHolder.getLocale()));
    }
}
