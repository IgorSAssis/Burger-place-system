package br.com.senior.burger_place.domain.occupation.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Builder
public class CreateOccupationDTO {
    @NotNull(message = "Occupation begin timestamp cannot be null")
    @PastOrPresent(message = "Occupation begin timestamp must be in the past or present")
    private LocalDateTime beginOccupation;
    @NotNull(message = "Occupation people count cannot be null")
    @Positive(message = "Occupation people count must be higher than zero")
    private Integer peopleCount;
    @NotNull(message = "Occupation board id cannot be null")
    private UUID boardId;
    private Set<UUID> customerIds;
}
