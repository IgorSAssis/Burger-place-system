package br.com.senior.burger_place.domain.orderItem.dto;

import br.com.senior.burger_place.domain.orderItem.OrderItemStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Builder
public class OrderItemDTO {
    private UUID id;
    private String productName;
    private String ingredients;
    private Integer amount;
    private String observation;
    private Integer boardNumber;
    private OrderItemStatus status;
    private UUID occupationId;
}
