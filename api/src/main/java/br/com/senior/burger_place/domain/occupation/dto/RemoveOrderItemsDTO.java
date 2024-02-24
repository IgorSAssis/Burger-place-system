package br.com.senior.burger_place.domain.occupation.dto;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;
import java.util.UUID;

public record RemoveOrderItemsDTO(
        @NotEmpty
        List<UUID> orderItems
) {
}
