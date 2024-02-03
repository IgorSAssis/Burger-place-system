package br.com.senior.burger_place.domain.board;

import br.com.senior.burger_place.domain.board.dto.BoardDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utils.BoardCreator;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static utils.BoardCreator.createBoard;

public class BoardConverterTest {
    private final BoardConverter boardConverter = new BoardConverter();
    private Board board;

    @BeforeEach
    void setUp() {
        this.board = createBoard();
    }

    @Test
    void toBoardDTO_whenCalled_shouldConvertToBoardDTO() {
        BoardDTO output = this.boardConverter.toBoardDTO(this.board);

        assertAll(
                () -> assertEquals(board.getId(), output.getId()),
                () -> assertEquals(board.getNumber(), output.getBoardNumber()),
                () -> assertEquals(board.getLocation(), output.getBoardLocation()),
                () -> assertEquals(board.getCapacity(), output.getBoardCapacity()),
                () -> assertEquals(board.getOccupied(), output.getOccupied()),
                () -> assertEquals(board.getActive(), output.getActive())
        );
    }
}
