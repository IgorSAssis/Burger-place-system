package br.com.senior.burger_place.domain.board;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static utils.BoardCreator.createBoard;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("BoardSpecification integration tests")
public class BoardSpecificationTest {
    @Autowired
    private BoardRepository boardRepository;
    @InjectMocks
    private BoardSpecification boardSpecification;

    @BeforeEach
    void setUp() {
        this.boardRepository.deleteAllInBatch();

        Board occupiedBoard = createBoard();
        occupiedBoard.setNumber(1);
        occupiedBoard.setCapacity(2);
        occupiedBoard.setLocation(BoardLocation.AREA_INTERNA);
        occupiedBoard.setActive(true);
        occupiedBoard.setOccupied(false);

        Board availableBoard = createBoard();
        availableBoard.setNumber(2);
        availableBoard.setCapacity(2);
        availableBoard.setLocation(BoardLocation.TERRACO);
        availableBoard.setActive(true);
        availableBoard.setOccupied(true);

        Board inactiveBoard = createBoard();
        inactiveBoard.setNumber(3);
        inactiveBoard.setCapacity(2);
        inactiveBoard.setLocation(BoardLocation.TERRACO);
        inactiveBoard.setActive(false);
        inactiveBoard.setOccupied(false);

        this.boardRepository.saveAll(
                List.of(occupiedBoard, availableBoard, inactiveBoard)
        );
    }

    @Test
    void applyFilters_whenBoardNumberIsNotNull_shouldFilterByBoardNumber() {

        Specification<Board> specification = this.boardSpecification.applyFilters(
                1, null, null, null, null
        );

        Page<Board> boardPage = this.boardRepository.findAll(specification, Pageable.ofSize(10));

        assertAll(
                () -> assertNotNull(boardPage.getContent())
        );
    }

    @AfterEach
    void tearDown() {
        this.boardRepository.deleteAll();
    }

}
