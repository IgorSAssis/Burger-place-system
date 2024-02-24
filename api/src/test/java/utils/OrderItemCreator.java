package utils;

import br.com.senior.burger_place.domain.orderItem.OrderItem;
import br.com.senior.burger_place.domain.orderItem.OrderItemStatus;
import br.com.senior.burger_place.domain.orderItem.dto.OrderItemDTO;

import java.util.UUID;

import static utils.OccupationCreator.createOccupation;
import static utils.ProductCreator.createProduct;

public class OrderItemCreator {

    public static OrderItem createOrderItem() {
        return OrderItem.builder()
                .id(UUID.randomUUID())
                .itemValue(30)
                .amount(2)
                .observation("Some observation")
                .status(OrderItemStatus.PRONTO)
                .product(createProduct())
                .occupation(createOccupation())
                .active(true)
                .build();
    }

    public static OrderItemDTO createOrderItemDTO() {
        return OrderItemDTO.builder()
                .id(UUID.randomUUID())
                .productName("Hamburguer")
                .ingredients("Pão, alface, tomate, carne, queijo")
                .amount(2)
                .observation("Some observation")
                .boardNumber(10)
                .status(OrderItemStatus.EM_ANDAMENTO)
                .occupationId(UUID.randomUUID())
                .build();
    }

}
