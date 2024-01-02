package br.com.senior.burger_place.domain.review.dto;

public record ReviewUpdateDTO(
        Integer grade,
        String comment
) {
}
