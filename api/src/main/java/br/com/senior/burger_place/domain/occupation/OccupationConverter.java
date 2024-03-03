package br.com.senior.burger_place.domain.occupation;

import br.com.senior.burger_place.domain.board.Board;
import br.com.senior.burger_place.domain.customer.Customer;
import br.com.senior.burger_place.domain.occupation.dto.*;
import br.com.senior.burger_place.domain.orderItem.OrderItem;
import org.springframework.stereotype.Component;

@Component
public class OccupationConverter {
    public ListOccupationDTO toListOccupationDTO(Occupation occupation) {
        return ListOccupationDTO.builder()
                .id(occupation.getId())
                .beginOccupation(occupation.getBeginOccupation())
                .endOccupation(occupation.getEndOccupation())
                .paymentForm(occupation.getPaymentForm())
                .peopleCount(occupation.getPeopleCount())
                .board(this.toBoardDTO(occupation.getBoard()))
                .build();
    }

    public OccupationDTO toOccupationDTO(Occupation occupation) {
        return OccupationDTO.builder()
                .id(occupation.getId())
                .beginOccupation(occupation.getBeginOccupation())
                .endOccupation(occupation.getEndOccupation())
                .paymentForm(occupation.getPaymentForm())
                .peopleCount(occupation.getPeopleCount())
                .board(this.toBoardDTO(occupation.getBoard()))
                .customers(occupation.getCustomers().stream().map(this::toCustomerDTO).toList())
                .orderItems(occupation.getOrderItems().stream().map(this::toOccupationItemDTO).toList())
                .build();
    }

    public BoardDTO toBoardDTO(Board board) {
        return BoardDTO.builder()
                .location(board.getLocation())
                .number(board.getNumber())
                .build();
    }

    public CustomerDTO toCustomerDTO(Customer customer) {
        return CustomerDTO.builder()
                .id(customer.getId())
                .name(customer.getName())
                .build();
    }

    public OccupationItemDTO toOccupationItemDTO(OrderItem item) {
        return OccupationItemDTO.builder()
                .id(item.getId())
                .productId(item.getProduct().getId())
                .productName(item.getProduct().getName())
                .ingredients(item.getProduct().getIngredients())
                .itemValue(item.getItemValue())
                .amount(item.getAmount())
                .status(item.getStatus())
                .observation(item.getObservation())
                .build();
    }
}
