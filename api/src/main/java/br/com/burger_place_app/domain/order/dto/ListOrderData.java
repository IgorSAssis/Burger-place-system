package br.com.burger_place_app.domain.order.dto;

import br.com.burger_place_app.domain.order.Order;

import java.time.LocalDateTime;

public record ListOrderData(
        Long id,
        LocalDateTime openedAt,
        LocalDateTime closedAt,
        String paymentForm,
        String status
) {
    public ListOrderData(Order order) {
        this(
                order.getId(),
                order.getOpenedAt(),
                order.getClosedAt(),
                order.getPaymentForm().name(),
                order.getStatus().name()
        );
    }
}
