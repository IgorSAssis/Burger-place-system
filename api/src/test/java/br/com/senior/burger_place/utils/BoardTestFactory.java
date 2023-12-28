package br.com.senior.burger_place.utils;

import br.com.senior.burger_place.domain.board.Board;
import br.com.senior.burger_place.domain.board.BoardLocation;

public class BoardTestFactory {
    public static Board boardFactory(Long id, Integer number, Integer capacity) {
        return new Board(
                id,
                number,
                capacity,
                BoardLocation.AREA_INTERNA,
                true
        );
    }
}
