package utils;

import br.com.senior.burger_place.domain.board.Board;
import br.com.senior.burger_place.domain.board.BoardLocation;
import br.com.senior.burger_place.domain.board.dto.BoardDTO;
import br.com.senior.burger_place.domain.board.dto.CreateBoardDTO;
import br.com.senior.burger_place.domain.board.dto.UpdateBoardDTO;

import java.util.UUID;

public class BoardCreator {
    public static Board createBoard() {
        return Board.builder()
                .id(UUID.randomUUID())
                .number(1)
                .capacity(2)
                .location(BoardLocation.AREA_INTERNA)
                .occupied(false)
                .build();
    }

    public static BoardDTO createBoardDTO() {
        return BoardDTO.builder()
                .id(UUID.randomUUID())
                .boardNumber(1)
                .boardCapacity(2)
                .boardLocation(BoardLocation.AREA_INTERNA)
                .build();
    }

    public static CreateBoardDTO createCreateBoardDTO() {
        return CreateBoardDTO.builder()
                .boardNumber(1)
                .capacity(2)
                .boardLocation(BoardLocation.AREA_INTERNA)
                .build();
    }

    public static UpdateBoardDTO createUpdateBoardDTO() {
        return UpdateBoardDTO.builder()
                .boardNumber(1)
                .capacity(2)
                .boardLocation(BoardLocation.AREA_INTERNA)
                .build();
    }
}
