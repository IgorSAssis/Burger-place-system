package br.com.senior.burger_place.domain.board.dto;

import br.com.senior.burger_place.domain.board.Board;
import br.com.senior.burger_place.domain.board.BoardLocation;

public record ListingBoardDTO(
        Long id,
        Integer number,
        Integer capacity,
        BoardLocation location
) {

    public ListingBoardDTO(Board board) {
        this(
                board.getId(),
                board.getNumber(),
                board.getCapacity(),
                board.getLocation()
        );
    }
}

