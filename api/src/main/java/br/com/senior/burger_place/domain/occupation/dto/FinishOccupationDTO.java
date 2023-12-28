package br.com.senior.burger_place.domain.occupation.dto;

import br.com.senior.burger_place.domain.occupation.PaymentForm;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record FinishOccupationDTO(
        @NotNull
        @FutureOrPresent
        LocalDateTime endOccupation,
        @NotNull
        PaymentForm paymentForm
) {
}
