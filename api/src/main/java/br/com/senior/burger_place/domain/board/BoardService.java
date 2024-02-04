package br.com.senior.burger_place.domain.board;

import br.com.senior.burger_place.domain.board.dto.CreateBoardDTO;
import br.com.senior.burger_place.domain.board.dto.UpdateBoardDTO;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class BoardService {
    @Autowired
    private BoardRepository boardRepository;
    @Autowired
    private BoardSpecification boardSpecification;

    public Page<Board> listBoards(
            Pageable pageable,
            Integer boardNumber,
            Integer capacity,
            BoardLocation boardLocation,
            Boolean active,
            Boolean occupied
    ) {
        Specification<Board> specification = this.boardSpecification.applyFilters(
                boardNumber,
                capacity,
                boardLocation,
                active,
                occupied
        );

        return this.boardRepository.findAll(specification, pageable);
    }

    public Board showBoard(
            @NonNull
            UUID boardId
    ) {
        return this.boardRepository.findById(boardId)
                .orElseThrow(() -> new EntityNotFoundException("Board does not exist"));
    }

    @Transactional
    public Board createBoard(
            @NonNull
            @Valid
            CreateBoardDTO dto
    ) {
        if (this.boardRepository.existsByNumberAndActiveTrue(dto.getBoardNumber())) {
            throw new DuplicateKeyException("Board with number already exists");
        }

        Board board = Board.builder()
                .number(dto.getBoardNumber())
                .capacity(dto.getCapacity())
                .location(dto.getBoardLocation())
                .build();

        return this.boardRepository.save(board);
    }

    @Transactional
    public Board updateBoard(
            @NonNull
            UUID boardId,
            @NonNull
            @Valid
            UpdateBoardDTO dto
    ) {
        Board board = this.boardRepository.findByIdAndActiveTrue(boardId)
                .orElseThrow(() -> new EntityNotFoundException("Board does not exists or is inactive"));

        if (board.getOccupied()) {
            throw new IllegalStateException("Cannot update occupied board");
        }

        if (this.boardRepository.existsByNumberAndActiveTrueAndIdNot(dto.getBoardNumber(), boardId)) {
            throw new IllegalArgumentException("Board number unavailable");
        }

        board.update(dto.getBoardLocation(), dto.getCapacity(), dto.getBoardNumber());

        return board;
    }

    @Transactional
    public void inactivateBoard(
            @NonNull
            UUID boardId
    ) {
        Board board = this.findActiveBoardById(boardId);
        board.inactivate();
    }

    @Transactional
    public void occupyBoard(
            @NonNull
            UUID boardId
    ) {
        Board board = this.findActiveBoardById(boardId);
        board.occupy();
    }

    @Transactional
    public void vacateBoard(
            @NonNull
            UUID boardId
    ) {
        Board board = this.findActiveBoardById(boardId);
        board.vacate();
    }

    private Board findActiveBoardById(UUID boardId) {
        return this.boardRepository.findByIdAndActiveTrue(boardId)
                .orElseThrow(() -> new EntityNotFoundException("Board does not exists or is inactive"));
    }
}
