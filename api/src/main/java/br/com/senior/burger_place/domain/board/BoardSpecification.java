package br.com.senior.burger_place.domain.board;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
class BoardSpecification {

    private Specification<Board> filterByBoardLocation(BoardLocation boardLocation) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(
                        root.get("location"),
                        boardLocation
                );
    }

    private Specification<Board> filterByCapacity(Integer capacity) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(
                        root.get("capacity"),
                        capacity
                );
    }

    private Specification<Board> filterByActive(Boolean active) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(
                        root.get("active"),
                        active
                );
    }

    private Specification<Board> filterByOccupied(Boolean occupied) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(
                        root.get("occupied"),
                        occupied
                );
    }

    private Specification<Board> filterByBoardNumber(Integer boardNumber) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(
                        root.get("number"),
                        boardNumber
                );
    }

    public Specification<Board> applyFilters(
            Integer boardNumber,
            Integer capacity,
            BoardLocation boardLocation,
            Boolean active,
            Boolean occupied
    ) {
        Specification<Board> boardSpecification = Specification.where(null);

        if (boardNumber != null) {
            boardSpecification = boardSpecification.and(this.filterByBoardNumber(boardNumber));
        }

        if (capacity != null) {
            boardSpecification = boardSpecification.and(this.filterByCapacity(capacity));
        }

        if (boardLocation != null) {
            boardSpecification = boardSpecification.and(this.filterByBoardLocation(boardLocation));
        }

        if (active != null) {
            boardSpecification = boardSpecification.and(this.filterByActive(active));
        }

        if (occupied != null) {
            boardSpecification = boardSpecification.and(this.filterByOccupied(occupied));
        }

        return boardSpecification;
    }

}
