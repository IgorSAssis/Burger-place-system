package br.com.senior.burger_place.domain.occupation.dto;

import br.com.senior.burger_place.domain.occupation.PaymentForm;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
public class ListOccupationDTO {
    private UUID id;
    private LocalDateTime beginOccupation;
    private LocalDateTime endOccupation;
    private PaymentForm paymentForm;
    private Integer peopleCount;
    private BoardDTO board;
}
