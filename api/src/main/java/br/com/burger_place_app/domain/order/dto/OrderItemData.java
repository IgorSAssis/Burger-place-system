package br.com.burger_place_app.domain.order.dto;

import br.com.burger_place_app.domain.order.OrderItem;

public record OrderItemData(
        Long id,
        Long productId,
        String productDescription,
        Double unitCost,
        Integer amount
) {
    public OrderItemData(OrderItem orderItem) {
        this(
                orderItem.getId(),
                orderItem.getProduct().getId(),
                orderItem.getProduct().getDescription(),
                orderItem.getItemValue(),
                orderItem.getQtdItens()
        );
    }
}
