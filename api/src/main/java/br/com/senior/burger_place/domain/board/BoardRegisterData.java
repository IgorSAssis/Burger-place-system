package br.com.senior.burger_place.domain.board;

import jakarta.validation.constraints.NotNull;

public record BoardRegisterData (
        @NotNull
        Integer number,
        @NotNull
        Integer capacity,
        @NotNull
        BoardLocation location
){
}
