package br.com.senior.burger_place.domain.board;

import br.com.senior.burger_place.domain.board.dto.CreateBoardDTO;
import br.com.senior.burger_place.domain.board.dto.UpdateBoardDTO;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static utils.BoardCreator.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("BoardService unit tests")
class BoardServiceTest {
    @InjectMocks
    private BoardService boardService;
    @Mock
    private BoardRepository boardRepositoryMocked;
    @Mock
    private BoardSpecification boardSpecificationMocked;

    @Nested
    @DisplayName("listBoards tests")
    class ListBoardsTest {
        @Test
        void listBoards_whenExistBoards_shouldReturnPageWithBoards() {
            List<Board> boards = List.of(createBoard());
            Page<Board> boardsPage = new PageImpl<>(boards, Pageable.ofSize(20), 20);

            mockApplyFilters(Specification.where(null));
            mockFindAll(boardsPage);

            List<Board> output = boardService.listBoards(
                    boardsPage.getPageable(), null, null, null, null, null
            ).toList();

            assertAll(
                    () -> assertNotNull(output),
                    () -> assertEquals(1, output.size()),
                    () -> assertEquals(boards.get(0), output.get(0))
            );
        }

        @Test
        void listBoards_whenDoNotExistBoards_shouldReturnEmptyPage() {
            Page<Board> boardsPage = new PageImpl<>(List.of(), Pageable.ofSize(20), 20);

            mockApplyFilters(Specification.where(null));
            mockFindAll(boardsPage);

            List<Board> output = boardService.listBoards(
                    boardsPage.getPageable(), null, null, null, null, null
            ).toList();

            assertAll(
                    () -> assertNotNull(output),
                    () -> assertTrue(output.isEmpty())
            );

        }

        private void mockApplyFilters(Specification<Board> expectedReturn) {
            when(boardSpecificationMocked.applyFilters(
                    null, null, null, null, null)
            ).thenReturn(expectedReturn);
        }

        private void mockFindAll(Page<Board> expectedReturn) {
            when(boardRepositoryMocked.findAll(eq(Specification.where(null)), any(Pageable.class)))
                    .thenReturn(expectedReturn);
        }
    }

    @Nested
    @DisplayName("showBoard tests")
    class ShowBoardTest {
        @Test
        void showBoard_whenIdIsNull_shouldThrowIllegalArgumentException() {
            assertThrows(IllegalArgumentException.class, () -> boardService.showBoard(null));
        }

        @Test
        void showBoard_whenBoardDoesNotExist_shouldThrowEntityNotFoundException() {
            String expectedErrorMessage = "Board does not exist";

            mockFindById(null);

            EntityNotFoundException exception = assertThrows(
                    EntityNotFoundException.class, () -> boardService.showBoard(UUID.randomUUID())
            );

            assertEquals(expectedErrorMessage, exception.getMessage());
        }

        @Test
        void showBoard_whenBoardExists_shouldReturnBoardDTO() {
            Board board = createBoard();

            mockFindById(board);

            Board output = boardService.showBoard(UUID.randomUUID());

            assertAll(
                    () -> assertNotNull(output),
                    () -> assertEquals(board, output)
            );
        }

        private void mockFindById(Board board) {
            when(boardRepositoryMocked.findById(any(UUID.class))).thenReturn(Optional.ofNullable(board));
        }
    }

    @Nested
    @DisplayName("createBoard tests")
    class CreateBoardTest {

        @Captor
        private ArgumentCaptor<Board> boardArgumentCaptor;

        @Test
        void createBoard_whenDTOIsNull_shouldThrowIllegalArgumentException() {
            assertThrows(
                    IllegalArgumentException.class, () -> boardService.createBoard(null)
            );
        }

        @Test
        void createBoard_whenBoardNumberAlreadyExists_shouldThrowDuplicateKeyException() {
            String expectedErrorMessage = "Board with number already exists";
            CreateBoardDTO createBoardDTO = createCreateBoardDTO();

            mockExistsByNumberAndActiveTrue(true);

            DuplicateKeyException exception = assertThrows(
                    DuplicateKeyException.class, () -> boardService.createBoard(createBoardDTO)
            );

            assertEquals(expectedErrorMessage, exception.getMessage());
        }

        @Test
        void createBoard_whenBoardDoesNotExist_shouldSaveAndReturnBoardDTO() {
            CreateBoardDTO createBoardDTO = createCreateBoardDTO();

            mockExistsByNumberAndActiveTrue(false);

            boardService.createBoard(createBoardDTO);
            verify(boardRepositoryMocked, times(1)).save(boardArgumentCaptor.capture());

            Board board = this.boardArgumentCaptor.getValue();

            assertAll(
                    () -> assertNotNull(board),
                    () -> assertEquals(createBoardDTO.getBoardNumber(), board.getNumber()),
                    () -> assertEquals(createBoardDTO.getBoardLocation(), board.getLocation()),
                    () -> assertEquals(createBoardDTO.getCapacity(), board.getCapacity()),
                    () -> assertTrue(board.getActive())
            );
        }

        private void mockExistsByNumberAndActiveTrue(Boolean expectedReturn) {
            when(boardRepositoryMocked.existsByNumberAndActiveTrue(anyInt())).thenReturn(expectedReturn);
        }
    }

    @Nested
    @DisplayName("updateBoard tests")
    class UpdateBoardTest {

        @Test
        void updateBoard_whenIdIsNull_shouldThrowIllegalArgumentException() {
            assertThrows(
                    IllegalArgumentException.class,
                    () -> boardService.updateBoard(null, null)
            );
        }

        @Test
        void updateBoard_whenDTOIsNull_shouldThrowIllegalArgumentException() {
            assertThrows(
                    IllegalArgumentException.class,
                    () -> boardService.updateBoard(UUID.randomUUID(), null)
            );
        }

        @Test
        void updateBoard_whenBoardDoesNotExistsOrIsInactive_shouldThrowEntityNotFoundException() {
            String expectedErrorMessage = "Board does not exists or is inactive";

            mockFindByIdAndActiveTrue(null);

            EntityNotFoundException exception = assertThrows(
                    EntityNotFoundException.class,
                    () -> boardService.updateBoard(UUID.randomUUID(), createUpdateBoardDTO())
            );

            assertEquals(expectedErrorMessage, exception.getMessage());
        }

        @Test
        void updateBoard_whenBoardIsOccupied_shouldThrowIllegalStateException() {
            String expectedErrorMessage = "Cannot update occupied board";
            Board board = createBoard();
            board.occupy();

            mockFindByIdAndActiveTrue(board);

            IllegalStateException exception = assertThrows(
                    IllegalStateException.class,
                    () -> boardService.updateBoard(UUID.randomUUID(), createUpdateBoardDTO())
            );

            assertEquals(expectedErrorMessage, exception.getMessage());
        }

        @Test
        void updateBoard_whenBoardNumberIsInUse_shouldThrowIllegalArgumentException() {
            String expectedErrorMessage = "Board number unavailable";
            Board board = createBoard();

            mockFindByIdAndActiveTrue(board);
            mockExistsByNumberAndActiveTrueAndIdNot(true);

            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> boardService.updateBoard(UUID.randomUUID(), createUpdateBoardDTO())
            );

            assertEquals(expectedErrorMessage, exception.getMessage());
        }

        @Test
        void updateBoard_whenCalledWithValidArguments_shouldUpdateBoard() {
            UpdateBoardDTO updateBoardDTO = createUpdateBoardDTO();
            Board boardSpy = spy(createBoard());

            mockFindByIdAndActiveTrue(boardSpy);
            mockExistsByNumberAndActiveTrueAndIdNot(false);

            Board output = boardService.updateBoard(UUID.randomUUID(), updateBoardDTO);

            assertAll(
                    () -> assertNotNull(output),
                    () -> assertEquals(updateBoardDTO.getBoardNumber(), output.getNumber()),
                    () -> assertEquals(updateBoardDTO.getBoardLocation(), output.getLocation()),
                    () -> assertEquals(updateBoardDTO.getCapacity(), output.getCapacity())
            );
            verify(boardSpy, times(1)).update(
                    updateBoardDTO.getBoardLocation(),
                    updateBoardDTO.getCapacity(),
                    updateBoardDTO.getBoardNumber()
            );
        }

        private void mockExistsByNumberAndActiveTrueAndIdNot(Boolean expectedReturn) {
            when(boardRepositoryMocked.existsByNumberAndActiveTrueAndIdNot(anyInt(), any(UUID.class)))
                    .thenReturn(expectedReturn);
        }
    }

    @Nested
    @DisplayName("inactivateBoard tests")
    class InactivateBoardTest {

        @Test
        void inactivateBoard_whenIdIsNull_shouldThrowIllegalArgumentException() {
            assertThrows(
                    IllegalArgumentException.class,
                    () -> boardService.inactivateBoard(null)
            );
        }

        @Test
        void inactivateBoard_whenBoardDoesNotExist_shouldThrowIllegalArgumentException() {
            String expectedErrorMessage = "Board does not exists or is inactive";

            mockFindByIdAndActiveTrue(null);

            EntityNotFoundException exception = assertThrows(
                    EntityNotFoundException.class,
                    () -> boardService.inactivateBoard(UUID.randomUUID())
            );

            assertEquals(expectedErrorMessage, exception.getMessage());
        }

        @Test
        void inactivateBoard_whenBoardExists_shouldInactivateBoard() {
            Board boardSpy = spy(createBoard());

            mockFindByIdAndActiveTrue(boardSpy);

            boardService.inactivateBoard(UUID.randomUUID());

            verify(boardSpy, times(1)).inactivate();
            assertFalse(boardSpy.getActive());
        }
    }

    @Nested
    @DisplayName("occupyBoard tests")
    class OccupyBoardTest {
        @Test
        void occupyBoard_whenIdIsNull_shouldThrowIllegalArgumentException() {
            assertThrows(
                    IllegalArgumentException.class,
                    () -> boardService.occupyBoard(null)
            );
        }

        @Test
        void occupyBoard_whenBoardDoesNotExist_shouldThrowIllegalArgumentException() {
            String expectedErrorMessage = "Board does not exists or is inactive";

            mockFindByIdAndActiveTrue(null);

            EntityNotFoundException exception = assertThrows(
                    EntityNotFoundException.class,
                    () -> boardService.occupyBoard(UUID.randomUUID())
            );

            assertEquals(expectedErrorMessage, exception.getMessage());
        }

        @Test
        void occupyBoard_whenBoardExists_shouldOccupyBoard() {
            Board boardSpy = spy(createBoard());

            mockFindByIdAndActiveTrue(boardSpy);

            boardService.occupyBoard(UUID.randomUUID());

            verify(boardSpy, times(1)).occupy();
            assertTrue(boardSpy.getOccupied());
        }
    }

    @Nested
    @DisplayName("vacateBoard tests")
    class VacateBoardTest {
        @Test
        void vacateBoard_whenIdIsNull_shouldThrowIllegalArgumentException() {
            assertThrows(
                    IllegalArgumentException.class,
                    () -> boardService.vacateBoard(null)
            );
        }

        @Test
        void vacateBoard_whenBoardDoesNotExist_shouldThrowIllegalArgumentException() {
            String expectedErrorMessage = "Board does not exists or is inactive";

            mockFindByIdAndActiveTrue(null);

            EntityNotFoundException exception = assertThrows(
                    EntityNotFoundException.class,
                    () -> boardService.vacateBoard(UUID.randomUUID())
            );

            assertEquals(expectedErrorMessage, exception.getMessage());
        }

        @Test
        void vacateBoard_whenBoardExists_shouldOccupyBoard() {
            Board board = createBoard();
            board.occupy();
            Board boardSpy = spy(board);

            mockFindByIdAndActiveTrue(boardSpy);

            boardService.vacateBoard(UUID.randomUUID());

            verify(boardSpy, times(1)).vacate();
            assertFalse(boardSpy.getOccupied());
        }
    }

    private void mockFindByIdAndActiveTrue(Board expectedReturn) {
        when(boardRepositoryMocked.findByIdAndActiveTrue(any(UUID.class))).thenReturn(Optional.ofNullable(expectedReturn));
    }
}