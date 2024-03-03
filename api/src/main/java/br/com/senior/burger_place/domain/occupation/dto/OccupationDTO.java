package br.com.senior.burger_place.domain.occupation.dto;

import br.com.senior.burger_place.domain.occupation.PaymentForm;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
public class OccupationDTO {
    private UUID id;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime beginOccupation;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endOccupation;
    private PaymentForm paymentForm;
    private Integer peopleCount;
    private BoardDTO board;
    private List<OccupationItemDTO> orderItems;
    private List<CustomerDTO> customers;
}
