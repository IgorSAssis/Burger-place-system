package br.com.senior.burger_place.domain.board.dto;

import br.com.senior.burger_place.domain.board.BoardLocation;

public record BoardUpdateDTO(
        Integer number,
        Integer capacity,
        BoardLocation location
) {
}
