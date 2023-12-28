package br.com.senior.burger_place.domain.board;

public record ListingDataBoard(
        Integer number,
        Integer capacity,
        BoardLocation location
) {

    public ListingDataBoard(Board board){
        this(board.getNumber(), board.getCapacity(), board.getLocation());
    }

}

