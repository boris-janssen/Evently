package com.evently.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(FormNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleFormNotFound(FormNotFoundException ex) {
        return Map.of("error", ex.getMessage());
    }

    @ExceptionHandler(EntryValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, List<com.evently.exception.FieldError>> handleEntryValidation(EntryValidationException ex) {
        return Map.of("errors", ex.getErrors());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, List<com.evently.exception.FieldError>> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        List<com.evently.exception.FieldError> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> new com.evently.exception.FieldError(fe.getField(), fe.getDefaultMessage()))
                .toList();

        return Map.of("errors", errors);
    }
}
