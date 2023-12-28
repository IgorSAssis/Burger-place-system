package br.com.senior.burger_place.utils;

import br.com.senior.burger_place.domain.occupation.Occupation;
import br.com.senior.burger_place.domain.occupation.OrderItem;
import br.com.senior.burger_place.domain.occupation.OrderItemStatus;
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
                null,
                new Product(),
                new Occupation(),
                true
        );
    }
}
