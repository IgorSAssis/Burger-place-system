package br.com.senior.burger_place.domain.review;

import jakarta.validation.constraints.NotNull;

public record ReviewRegisterData(
        @NotNull
        int grade,
        String comment

) {

}
