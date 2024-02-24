package br.com.senior.burger_place.domain.occupation.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.UUID;

public record CreateOrderItemDTO(
        @NotNull
        @Positive
        UUID productId,
        @NotNull
        @Positive
        Integer amount,
        String observation
) {
}
