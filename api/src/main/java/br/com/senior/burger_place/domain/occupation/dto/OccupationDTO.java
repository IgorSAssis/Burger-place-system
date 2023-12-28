package br.com.senior.burger_place.domain.occupation.dto;

import br.com.senior.burger_place.domain.occupation.Occupation;
import br.com.senior.burger_place.domain.occupation.PaymentForm;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public record OccupationDTO(
        Long id,
        LocalDateTime beginOccupation,
        LocalDateTime endOccupation,
        PaymentForm paymentForm,
        Integer peopleCount,
        OccupationBoardDTO board,
        List<OrderItemDTO> orderItems,
        List<CustomerOccupationDTO> customers
) {
    public OccupationDTO(Occupation occupation) {
        this(
                occupation.getId(),
                occupation.getBeginOccupation(),
                occupation.getEndOccupation(),
                occupation.getPaymentForm(),
                occupation.getPeopleCount(),
                new OccupationBoardDTO(occupation.getBoard()),
                occupation.getOrderItems() != null
                        ? occupation.getOrderItems().stream().map(OrderItemDTO::new).toList()
                        : null,
                occupation.getCustomers() != null
                        ? occupation.getCustomers().stream().map(CustomerOccupationDTO::new).toList()
                        : null
        );
    }
}
