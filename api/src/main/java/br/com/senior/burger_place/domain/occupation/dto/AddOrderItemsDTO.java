package br.com.senior.burger_place.domain.occupation.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record AddOrderItemsDTO(
        @NotEmpty
        @Valid
        List<CreateOrderItemDTO> orderItems
) {
}
