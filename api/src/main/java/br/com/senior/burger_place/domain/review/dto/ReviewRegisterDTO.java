package br.com.senior.burger_place.domain.review.dto;

import jakarta.validation.constraints.NotNull;

public record ReviewRegisterDTO(
        @NotNull
        Integer grade,
        String comment

) {

}
