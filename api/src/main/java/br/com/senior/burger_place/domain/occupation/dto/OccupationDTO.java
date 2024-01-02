package br.com.senior.burger_place.domain.occupation.dto;

import br.com.senior.burger_place.domain.occupation.Occupation;
import br.com.senior.burger_place.domain.occupation.PaymentForm;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;
import java.util.List;

public record OccupationDTO(
        Long id,
        @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
        LocalDateTime beginOccupation,
        @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
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
