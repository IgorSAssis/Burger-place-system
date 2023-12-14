package br.com.burger_place_app.domain.order.dto;

import jakarta.validation.constraints.Positive;

public record UpdateOrderItemData(
        @Positive
        Integer amount
) {
}
