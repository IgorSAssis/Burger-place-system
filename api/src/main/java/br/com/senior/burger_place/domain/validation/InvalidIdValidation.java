package br.com.senior.burger_place.domain.validation;

import java.util.UUID;

public class InvalidIdValidation {
    public static void validate(UUID id) {
        InvalidIdValidation.validate(id, "ID inválida");
    }

    public static void validate(UUID id, String message) {
        if (id == null) {
            throw new IllegalArgumentException(message);
        }
    }
}
