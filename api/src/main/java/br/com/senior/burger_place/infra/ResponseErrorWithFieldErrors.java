package br.com.senior.burger_place.infra;

import org.springframework.validation.FieldError;

public record ResponseErrorWithFieldErrors(String field, String message) {
    public ResponseErrorWithFieldErrors(FieldError error) {
        this(error.getField(), error.getDefaultMessage());
    }
}
