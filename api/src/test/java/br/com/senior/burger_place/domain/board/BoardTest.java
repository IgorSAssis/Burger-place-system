package br.com.senior.burger_place.domain.board;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static utils.BoardCreator.createBoard;

@DisplayName("Board unit tests")
public class BoardTest {

    private Board board;

    @BeforeEach
    void setUp() {
        this.board = createBoard();
    }

    @Nested
    @DisplayName("inactivate tests")
    class InactivateTest {
        @Test
        void inactivate_whenBoardIsInactive_shouldThrowIllegalStateException() {
            String expectedErrorMessage = "Board already inactive";

            board.inactivate();

            IllegalStateException exception = assertThrows(
                    IllegalStateException.class,
                    () -> board.inactivate()
            );
            assertEquals(expectedErrorMessage, exception.getMessage());
        }

        @Test
        void inactivate_whenBoardIsOccupied_shouldThrowIllegalStateException() {
            String expectedErrorMessage = "Cannot inactivate a occupied board";

            board.occupy();

            IllegalStateException exception = assertThrows(
                    IllegalStateException.class,
                    () -> board.inactivate()
            );
            assertEquals(expectedErrorMessage, exception.getMessage());
        }

        @Test
        void inactivate_whenHasValidState_shouldInactivate() {
            board.inactivate();

            assertFalse(board.getActive());
        }
    }

    @Nested
    @DisplayName("occupyy tests")
    class OccupyTest {
        @Test
        void occupy_whenBoardIsOccupied_shouldThrowIllegalStateException() {
            String expectedErrorMessage = "Board already occupied";

            board.occupy();

            IllegalStateException exception = assertThrows(
                    IllegalStateException.class,
                    () -> board.occupy()
            );
            assertEquals(expectedErrorMessage, exception.getMessage());
        }

        @Test
        void occupy_whenBoardIsNotOccupied_shouldOccupyBoard() {
            board.occupy();

            assertTrue(board.getOccupied());
        }
    }

    @Nested
    @DisplayName("vacate tests")
    class VacateTest {
        @Test
        void vacate_whenBoardIsNotOccupied_shouldThrowIllegalStateException() {
            String expectedErrorMessage = "Board is not occupied";

            IllegalStateException exception = assertThrows(
                    IllegalStateException.class,
                    () -> board.vacate()
            );
            assertEquals(expectedErrorMessage, exception.getMessage());
        }

        @Test
        void vacate_whenBoardIsOccupied_shouldVacateBoard() {
            board.occupy();
            board.vacate();
            assertFalse(board.getOccupied());
        }
    }
}
