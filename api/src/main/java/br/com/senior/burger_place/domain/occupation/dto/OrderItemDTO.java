package br.com.senior.burger_place.domain.occupation.dto;

import br.com.senior.burger_place.domain.orderItem.OrderItem;
import br.com.senior.burger_place.domain.orderItem.OrderItemStatus;

public record OrderItemDTO(
        Long id,
        Long productId,
        String productName,
        String ingredients,
        Double itemValue,
        Integer amount,
        OrderItemStatus status,
        String observation
) {
    public OrderItemDTO(OrderItem orderItem) {
        this(
                orderItem.getId(),
                orderItem.getProduct().getId(),
                orderItem.getProduct().getName(),
                orderItem.getProduct().getIngredients(),
                orderItem.getItemValue(),
                orderItem.getAmount(),
                orderItem.getStatus(),
                orderItem.getObservation()
        );
    }
}
