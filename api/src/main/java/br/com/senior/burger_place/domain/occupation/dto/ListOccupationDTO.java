package br.com.senior.burger_place.domain.occupation.dto;

import br.com.senior.burger_place.domain.occupation.Occupation;
import br.com.senior.burger_place.domain.occupation.PaymentForm;

import java.time.LocalDateTime;
import java.util.UUID;

public record ListOccupationDTO(
        UUID id,
        LocalDateTime beginOccupation,
        LocalDateTime endOccupation,
        PaymentForm paymentForm,
        Integer peopleCount,
        OccupationBoardDTO board
) {
    public ListOccupationDTO(Occupation occupation) {
        this(
                occupation.getId(),
                occupation.getBeginOccupation(),
                occupation.getEndOccupation(),
                occupation.getPaymentForm(),
                occupation.getPeopleCount(),
                new OccupationBoardDTO(occupation.getBoard())
        );
    }
}
