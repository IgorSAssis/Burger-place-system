package br.com.senior.burger_place.domain.orderItem;

import br.com.senior.burger_place.domain.orderItem.dto.OrderItemDTO;
import org.springframework.stereotype.Component;

@Component
public class OrderItemConverter {
    public OrderItemDTO toOrderItemsDTO(OrderItem item) {
        return OrderItemDTO.builder()
                .id(item.getId())
                .occupationId(item.getOccupation().getId())
                .boardNumber(item.getOccupation().getBoard().getNumber())
                .ingredients(item.getProduct().getIngredients())
                .observation(item.getObservation())
                .productName(item.getProduct().getName())
                .status(item.getStatus())
                .amount(item.getAmount())
                .build();
    }
}
