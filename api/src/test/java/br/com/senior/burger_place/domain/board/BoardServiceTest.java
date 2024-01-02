package br.com.senior.burger_place.domain.board;

import br.com.senior.burger_place.domain.board.dto.BoardRegisterDTO;
import br.com.senior.burger_place.domain.board.dto.BoardUpdateDTO;
import br.com.senior.burger_place.domain.occupation.Occupation;
import br.com.senior.burger_place.domain.occupation.OccupationRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static br.com.senior.burger_place.domain.board.BoardLocation.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BoardServiceTest {

    @InjectMocks
    private BoardService boardService;
    @Mock
    private BoardRepository boardRepository;
    @Mock
    private OccupationRepository occupationRepository;
    @Captor
    private ArgumentCaptor<Board> boardCaptor;
    @Captor
    private ArgumentCaptor<BoardUpdateDTO> boardUpdateDTOCaptor;
    @Captor
    private ArgumentCaptor<Pageable> pageableCaptor;
    @Captor
    private ArgumentCaptor<BoardLocation> boardLocationCaptor;

    @Test
    public void addBoard_whenExistsBoardWithSameNumber_shouldThrowException() {
        BoardRegisterDTO dto = new BoardRegisterDTO(2, 4, VARANDA);

        when(boardRepository.existsByNumber(dto.number())).thenReturn(false);

        boardService.addBoard(dto);
        verify(boardRepository, times(1)).save(boardCaptor.capture());

        Board boardCaptured = boardCaptor.getValue();

        assertEquals(dto.number(), boardCaptured.getNumber());
        assertEquals(dto.capacity(), boardCaptured.getCapacity());
        assertEquals(dto.location(), boardCaptured.getLocation());
    }

    @Test
    public void addBoard_whenNotExistsBoardWithSameNumber_shouldSaveBoard() {
        BoardRegisterDTO dto = new BoardRegisterDTO(2, 4, VARANDA);
        Board board = new Board(dto);

        when(boardRepository.existsByNumber(dto.number())).thenReturn(true);

        DuplicateKeyException exception = assertThrows(DuplicateKeyException.class, () -> boardService.addBoard(dto));
        assertEquals("Já existe uma mesa cadastrada com esse número", exception.getMessage());

        verify(boardRepository, never()).save(board);
    }

    @Test
    public void updateBoard_whenOptionalBoardIsNull_shouldThrowException() {
        Long boardId = 1l;
        BoardUpdateDTO boardUpdateDTO = new BoardUpdateDTO(2, 5, SACADA);

        when(boardRepository.findById(boardId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> boardService.updateBoard(boardId, boardUpdateDTO));
        assertEquals("Mesa não cadastrada", exception.getMessage());
    }

    @Test
    public void updateBoard_whenOptionalBoardIsValid_shouldCalledUpdateInformationMethod() {
        Long boardId = 1L;
        BoardUpdateDTO boardUpdateDTO = new BoardUpdateDTO(2, 5, SACADA);
        Board existingBoard = mock(Board.class);

        Optional<Board> optionalBoard = Optional.of(existingBoard);
        when(boardRepository.findById(boardId)).thenReturn(optionalBoard);

        boardService.updateBoard(boardId, boardUpdateDTO);

        verify(existingBoard, times(1)).updateInformation(boardUpdateDTOCaptor.capture());

        BoardUpdateDTO capturedData = boardUpdateDTOCaptor.getValue();

        assertEquals(boardUpdateDTO.number(), capturedData.number());
        assertEquals(boardUpdateDTO.capacity(), capturedData.capacity());
        assertEquals(boardUpdateDTO.location(), capturedData.location());
    }

    @Test
    public void listBoardsById_whenBoardIsNull_shouldThrowException() {
        Long boardId = 1l;

        when(boardRepository.getReferenceByIdAndActiveTrue(boardId)).thenReturn(null);

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> boardService.listBoardsById(boardId));
        assertEquals("Mesa não existe", exception.getMessage());
    }

    @Test
    public void listBoardsById_whenBoardIsValid_shouldReturnBoard() {
        Long boardId = 1l;
        BoardRegisterDTO dto = new BoardRegisterDTO(2, 5, TERRACO);
        Board board = new Board(dto);

        when(boardRepository.getReferenceByIdAndActiveTrue(boardId)).thenReturn(board);

        Board result = boardService.listBoardsById(boardId);
        assertEquals(board, result);
    }

    @Test
    public void verifyOccupiedBoard_whenBoardIsOccupied_shouldThrowException() {
        Long boardId = 1l;
        Board board = mock(Board.class);

        when(boardRepository.getReferenceByIdAndActiveTrue(boardId)).thenReturn(board);
        when(boardRepository.isBoardOccupied(boardId)).thenReturn(true);

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> boardService.verifyOccupiedBoard(boardId));
        assertEquals("A mesa já está ocupada", exception.getMessage());
        verify(boardRepository, times(1)).getReferenceByIdAndActiveTrue(boardId);
    }

    @Test
    public void verifyOccupiedBoard_whenBoardNotIsOccupied_shouldReturnBoard() {
        Long boardId = 1l;
        BoardRegisterDTO dto = new BoardRegisterDTO(3, 3, AREA_INTERNA);
        Board board = new Board(dto);

        when(boardRepository.getReferenceByIdAndActiveTrue(boardId)).thenReturn(board);
        when(boardRepository.isBoardOccupied(boardId)).thenReturn(false);

        Board result = boardService.verifyOccupiedBoard(boardId);

        assertEquals(board, result);
        verify(boardRepository, times(1)).getReferenceByIdAndActiveTrue(boardId);
    }

    @Test
    public void listAllBoards_shouldReturnAllBoardsAvailable() {
        Pageable pageable = Pageable.unpaged();

        boardService.listAllBoards(pageable);

        verify(boardRepository, times(1)).findAllBoardsAvailable(pageableCaptor.capture());
        assertEquals(pageable, pageableCaptor.getValue());
    }

    @Test
    public void listAvailableBoardsByLocationAndOccupation_whenBoardsIsEmpty_shouldThrowException() {
        BoardLocation location = VARANDA;
        Pageable pageable = Pageable.unpaged();

        when(boardRepository.findByLocationAndActiveTrue(location, pageable)).thenReturn(Page.empty());

        Page<Board> output = boardService.listAvailableBoardsByLocationAndOccupation(location, pageable);
        assertEquals(0, output.getSize());
        verify(boardRepository, times(1)).findByLocationAndActiveTrue(location, pageable);
    }

    @Test
    public void listAvailableBoardsByLocationAndOccupation_whenBoardsIsNotEmpty_shouldReturnBoardsPage() {
        BoardLocation location = VARANDA;
        Pageable pageable = Pageable.unpaged();
        Board board1 = new Board(mock(BoardRegisterDTO.class));
        Board board2 = new Board(mock(BoardRegisterDTO.class));

        List<Board> boarsList = Arrays.asList(board1, board2);
        Page<Board> boardPage = new PageImpl<>(boarsList);

        when(boardRepository.findByLocationAndActiveTrue(boardLocationCaptor.capture(), pageableCaptor.capture())).thenReturn(boardPage);

        Page<Board> result = boardService.listAvailableBoardsByLocationAndOccupation(location, pageable);

        assertEquals(boardPage, result);
        assertEquals(location, boardLocationCaptor.getValue());
        assertEquals(pageable, pageableCaptor.getValue());

        verify(boardRepository, times(1)).findByLocationAndActiveTrue(location, pageable);
    }

    @Test
    public void listAvailableBoardsByCapacityAndOccupation_whenAllBoardsIsValid_shouldReturnBoardsPage() {
        Pageable pageable = Pageable.unpaged();
        BoardRegisterDTO dto1 = new BoardRegisterDTO(1, 3, VARANDA);
        BoardRegisterDTO dto2 = new BoardRegisterDTO(2, 3, TERRACO);
        BoardRegisterDTO dto3 = new BoardRegisterDTO(3, 3, VARANDA);
        Board board1 = new Board(dto1);
        Board board2 = new Board(dto2);
        Board board3 = new Board(dto3);

        List<Board> boarsList = Arrays.asList(board1, board2, board3);
        Page<Board> boardPage = new PageImpl<>(boarsList);
        when(boardRepository.findByCapacityAndActiveTrue(3, pageable)).thenReturn(boardPage);

        Page<Board> result = boardService.listAvailableBoardsByCapacityAndOccupation(3, pageable);

        assertEquals(3, result.getTotalElements());

        verify(boardRepository, times(1)).findByCapacityAndActiveTrue(3, pageable);
        verify(occupationRepository, times(3)).findFirstByBoardIdOrderByBeginOccupationDesc(any());
    }

    @Test
    public void listAvailableBoardsByCapacityAndOccupation_whenBoardsIsEmpty_shouldThrowException() {
        Pageable pageable = Pageable.unpaged();

        when(boardRepository.findByCapacityAndActiveTrue(4, pageable)).thenReturn(Page.empty());

        Page<Board> output = boardService.listAvailableBoardsByCapacityAndOccupation(4, pageable);
        assertEquals(0, output.getSize());
        verify(boardRepository, times(1)).findByCapacityAndActiveTrue(4, pageable);
    }

    @Test
    public void listAvailableBoardsByLocationAndCapacityAndOccupation_whenBoardsIsEmpty_shouldThrowException() {
        Pageable pageable = Pageable.unpaged();

        when(boardRepository.findByLocationAndCapacityAndActiveTrue(VARANDA, 4, pageable)).thenReturn(Page.empty());

        Page<Board> output = boardService.listAvailableBoardsByLocationAndCapacityAndOccupation(VARANDA, 4, pageable);
        assertEquals(0, output.getSize());
        verify(boardRepository, times(1)).findByLocationAndCapacityAndActiveTrue(VARANDA, 4, pageable);
    }

    @Test
    public void listAvailableBoardsByLocationAndCapacityAndOccupation_whenAllBoardsIsValid_shouldReturnBoardsPage() {
        Pageable pageable = Pageable.unpaged();
        BoardRegisterDTO dto1 = new BoardRegisterDTO(1, 4, VARANDA);
        BoardRegisterDTO dto3 = new BoardRegisterDTO(2, 4, VARANDA);
        Board board1 = new Board(dto1);
        Board board3 = new Board(dto3);

        List<Board> boarsList = Arrays.asList(board1, board3);
        Page<Board> boardPage = new PageImpl<>(boarsList);
        when(boardRepository.findByLocationAndCapacityAndActiveTrue(VARANDA, 4, pageable)).thenReturn(boardPage);

        Page<Board> result = boardService.listAvailableBoardsByLocationAndCapacityAndOccupation(VARANDA, 4, pageable);

        assertEquals(2, result.getTotalElements());

        verify(boardRepository, times(1)).findByLocationAndCapacityAndActiveTrue(VARANDA, 4, pageable);
        verify(occupationRepository, times(2)).findFirstByBoardIdOrderByBeginOccupationDesc(any());
    }

}