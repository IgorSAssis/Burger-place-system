package br.com.senior.burger_place.domain.board;

public record BoardUpdateData(
        Integer number,
        Integer capacity,
        BoardLocation location
) {
}
