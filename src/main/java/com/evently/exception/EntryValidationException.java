package com.evently.exception;

import java.util.List;

public class EntryValidationException extends RuntimeException {

    private final List<FieldError> errors;

    public EntryValidationException(List<FieldError> errors) {
        super("Entry validation failed");
        this.errors = errors;
    }

    public List<FieldError> getErrors() {
        return errors;
    }
}
