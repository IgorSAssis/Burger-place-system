package br.com.senior.burger_place.domain.validation;

public class InvalidIdValidation {
    public static void validate(Long id) {
        InvalidIdValidation.validate(id, "ID inválida");
    }

    public static void validate(Long id, String message) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException(message);
        }
    }
}
