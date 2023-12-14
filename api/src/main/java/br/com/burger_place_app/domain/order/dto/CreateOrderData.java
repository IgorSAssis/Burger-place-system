package br.com.burger_place_app.domain.order.dto;

import br.com.burger_place_app.domain.order.PaymentForm;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record CreateOrderData(
        @NotNull
        PaymentForm paymentForm,
        @NotNull
        Long clientId,
        @NotEmpty
        List<CreateOrderItemData> orderItems
) {
}
