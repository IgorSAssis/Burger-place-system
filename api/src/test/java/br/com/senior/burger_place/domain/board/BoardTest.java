package br.com.senior.burger_place.domain.board;

import br.com.senior.burger_place.domain.board.dto.BoardRegisterDTO;
import br.com.senior.burger_place.domain.board.dto.BoardUpdateDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static br.com.senior.burger_place.domain.board.BoardLocation.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class BoardTest {

    @Test
    public void updateInformation_whenNoNullValues_shouldUpdateData(){
        Board board = new Board(new BoardRegisterDTO(1, 3, TERRACO));
        BoardUpdateDTO newDto = new BoardUpdateDTO(2, 4, VARANDA);

        board.updateInformation(newDto);

        assertEquals(2, board.getNumber());
        assertEquals(4, board.getCapacity());
        assertEquals(VARANDA, board.getLocation());
    }

    @Test
    public void updateInformation_whenNullValues_shoulNotdUpdateData(){
        Board board = new Board(new BoardRegisterDTO(1, 3, TERRACO));
        BoardUpdateDTO newDto = new BoardUpdateDTO(null, null, null);

        board.updateInformation(newDto);

        assertEquals(1, board.getNumber());
        assertEquals(3, board.getCapacity());
        assertEquals(TERRACO, board.getLocation());
    }

    @Test
    public void inactivate_whenInactivateIsCalled_activeAttributeShouldBeFalse(){
        Board board = new Board(new BoardRegisterDTO(2, 3, TERRACO));

        assertTrue(board.isActive());
        board.inactivate();
        assertFalse(board.isActive());
    }
}