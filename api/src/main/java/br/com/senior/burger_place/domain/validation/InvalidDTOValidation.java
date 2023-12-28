package br.com.senior.burger_place.domain.validation;

public class InvalidDTOValidation {
    public static void validate(Object DTO) {
        if (DTO == null) {
            throw new IllegalArgumentException("DTO inválido");
        }
    }
}
