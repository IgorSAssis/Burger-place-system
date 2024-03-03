package br.com.senior.burger_place.domain.occupation.dto;

import br.com.senior.burger_place.domain.orderItem.OrderItemStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Builder
public class OccupationItemDTO {
    private UUID id;
    private UUID productId;
    private String productName;
    private String ingredients;
    private Double itemValue;
    private Integer amount;
    private OrderItemStatus status;
    private String observation;
}
