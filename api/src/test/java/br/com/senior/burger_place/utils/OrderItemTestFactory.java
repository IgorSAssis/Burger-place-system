package br.com.senior.burger_place.utils;

import br.com.senior.burger_place.domain.occupation.Occupation;
import br.com.senior.burger_place.domain.orderItem.OrderItem;
import br.com.senior.burger_place.domain.orderItem.OrderItemStatus;
import br.com.senior.burger_place.domain.product.Product;

public class OrderItemTestFactory {
    public static OrderItem orderItemFactory(Long id) {
        return OrderItemTestFactory.orderItemFactory(id, 2);
    }

    public static OrderItem orderItemFactory(Long id, Integer amount) {
        return new OrderItem(
                id,
                amount,
                20.3,
                OrderItemStatus.RECEBIDO,
                "Sem tomate",
                ProductTestFactory.productFactory(1L),
                OccupationTestFactory.openedOccupationFactory(1L),
                true
        );
    }
}
