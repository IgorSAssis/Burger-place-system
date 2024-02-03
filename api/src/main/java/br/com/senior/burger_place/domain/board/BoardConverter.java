package br.com.senior.burger_place.domain.board;

import br.com.senior.burger_place.domain.board.dto.BoardDTO;
import org.springframework.stereotype.Component;

@Component
public class BoardConverter {
    public BoardDTO toBoardDTO(Board board) {
        return BoardDTO
                .builder()
                .id(board.getId())
                .boardNumber(board.getNumber())
                .boardLocation(board.getLocation())
                .boardCapacity(board.getCapacity())
                .occupied(board.getOccupied())
                .active(board.getActive())
                .build();
    }
}
