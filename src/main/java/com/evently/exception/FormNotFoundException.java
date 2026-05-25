package com.evently.exception;

public class FormNotFoundException extends RuntimeException {

    public FormNotFoundException(String id) {
        super("Form not found with id: " + id);
    }
}
