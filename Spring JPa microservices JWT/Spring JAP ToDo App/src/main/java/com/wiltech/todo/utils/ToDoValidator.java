/*
 * (c) Midland Software Limited 2019
 * Name     : ToDoValodator.java
 * Author   : ferraciolliw
 * Date     : 24 Sep 2019
 */
package com.wiltech.todo.utils;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.wiltech.todo.entities.ToDo;

/**
 * The type To do validator.
 */
public class ToDoValidator implements Validator {

    @Override
    public boolean supports(final Class<?> clazz) {
        return ToDo.class.equals(clazz);
    }

    @Override
    public void validate(final Object target, final Errors errors) {

        final ToDo toDo = (ToDo) target;

        final String priority = toDo.getPriority();

        if (!"high".equals(priority)) {
            errors.rejectValue("priority", "Priority is not valid");
        }
    }
}
