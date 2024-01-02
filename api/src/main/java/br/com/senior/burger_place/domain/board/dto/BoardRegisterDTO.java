package br.com.senior.burger_place.domain.board.dto;

import br.com.senior.burger_place.domain.board.BoardLocation;
import jakarta.validation.constraints.NotNull;

public record BoardRegisterDTO(
        @NotNull
        Integer number,
        @NotNull
        Integer capacity,
        @NotNull
        BoardLocation location
){
}
