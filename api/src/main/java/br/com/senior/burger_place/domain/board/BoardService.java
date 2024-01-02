package br.com.senior.burger_place.domain.board;

import br.com.senior.burger_place.domain.board.dto.BoardRegisterDTO;
import br.com.senior.burger_place.domain.board.dto.BoardUpdateDTO;
import br.com.senior.burger_place.domain.occupation.Occupation;
import br.com.senior.burger_place.domain.occupation.OccupationRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BoardService {

    @Autowired
    BoardRepository repository;

    @Autowired
    OccupationRepository occupationRepository;

    public Board addBoard(BoardRegisterDTO data) {
        if (repository.existsByNumber(data.number())) {
            throw new DuplicateKeyException("Já existe uma mesa cadastrada com esse número");
        }
        return repository.save(new Board(data));
    }

    public void updateBoard(Long id, BoardUpdateDTO data) {
        Optional<Board> optionalBoard = repository.findById(id);
        if (optionalBoard.isEmpty()) {
            throw new EntityNotFoundException("Mesa não cadastrada");
        }
        Board board = optionalBoard.get();
        board.updateInformation(data);
    }

    public Board listBoardsById(Long id) {
        Board board = repository.getReferenceByIdAndActiveTrue(id);
        if (board == null) {
            throw new EntityNotFoundException("Mesa não existe");
        }
        return board;
    }

    public Board verifyOccupiedBoard(Long id) {
        Board board = listBoardsById(id);
        if (repository.isBoardOccupied(id)) {
            throw new EntityNotFoundException("A mesa já está ocupada");
        }
        return board;
    }

    public Page<Board> listAllBoards(Pageable pageable) {
        return repository.findAllBoardsAvailable(pageable);

    }

    public Page<Board> listAvailableBoardsByLocationAndOccupation(BoardLocation location, Pageable pageable) {
        Page<Board> boards = repository.findByLocationAndActiveTrue(location, pageable);
        List<Board> filteredBoards = boards.getContent().stream()
                .filter(board -> !isBoardOccupied(board.getId()))
                .collect(Collectors.toList());
        return new PageImpl<>(filteredBoards, pageable, filteredBoards.size());
    }

    public Page<Board> listAvailableBoardsByCapacityAndOccupation(Integer capacity, Pageable pageable) {
        Page<Board> boards = repository.findByCapacityAndActiveTrue(capacity, pageable);
        List<Board> filteredBoards = boards.getContent().stream()
                .filter(board -> !isBoardOccupied(board.getId()))
                .collect(Collectors.toList());
        return new PageImpl<>(filteredBoards, pageable, filteredBoards.size());
    }

    public Page<Board> listAvailableBoardsByLocationAndCapacityAndOccupation(
            BoardLocation location, Integer capacity, Pageable pageable) {

        Page<Board> boards = repository.findByLocationAndCapacityAndActiveTrue(location, capacity, pageable);
        List<Board> filteredBoards = boards.getContent().stream()
                .filter(board -> !isBoardOccupied(board.getId()))
                .collect(Collectors.toList());

        return new PageImpl<>(filteredBoards, pageable, filteredBoards.size());
    }

    public boolean isBoardOccupied(Long boardId) {
        Occupation occupation = occupationRepository.findFirstByBoardIdOrderByBeginOccupationDesc(boardId);
        return occupation != null && occupation.getEndOccupation() == null;
    }
}
