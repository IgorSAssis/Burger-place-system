package br.com.burger_place_app.domain.order.dto;

import br.com.burger_place_app.domain.order.Order;

import java.time.LocalDateTime;
import java.util.List;

public record OrderData(
        Long id,
        LocalDateTime openedAt,
        LocalDateTime closedAt,
        String paymentForm,
        String status,
        CustomerOrderData customer,
        List<OrderItemData> orderItems
) {
    public OrderData(Order order) {
        this(
                order.getId(),
                order.getOpenedAt(),
                order.getClosedAt(),
                order.getPaymentForm().name(),
                order.getStatus().name(),
                new CustomerOrderData(order.getCustomer()),
                order.getOrderItems().stream().map(OrderItemData::new).toList()
        );
    }
}
