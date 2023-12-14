package br.com.burger_place_app.domain.order.dto;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record RemoveOrderItemsData(
        @NotEmpty
        List<Long> orderItems
) {
}
