package br.com.senior.burger_place.domain.orderItem.dto;

import br.com.senior.burger_place.domain.orderItem.OrderItem;
import br.com.senior.burger_place.domain.orderItem.OrderItemStatus;

import java.util.UUID;

public record ListOrderItemsDTO(
        UUID id,
        String productName,
        String ingredients,
        Integer amount,
        String observation,
        Integer boardNumber,
        OrderItemStatus status,
        UUID occupationId
) {
    public ListOrderItemsDTO(OrderItem orderItem) {
        this(
                orderItem.getId(),
                orderItem.getProduct().getName(),
                orderItem.getProduct().getIngredients(),
                orderItem.getAmount(),
                orderItem.getObservation(),
                orderItem.getOccupation().getBoard().getNumber(),
                orderItem.getStatus(),
                orderItem.getOccupation().getId()
        );
    }
}
