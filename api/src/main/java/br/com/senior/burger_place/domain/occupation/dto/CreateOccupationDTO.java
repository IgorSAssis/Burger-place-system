package br.com.senior.burger_place.domain.occupation.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;

import java.time.LocalDateTime;
import java.util.Set;

public record CreateOccupationDTO(
        @NotNull
        @PastOrPresent
        LocalDateTime beginOccupation,
        @NotNull
        @Positive
        Integer peopleCount,
        @NotNull
        @Positive
        Long boardId,
        Set<Long> customerIds

) {
}
