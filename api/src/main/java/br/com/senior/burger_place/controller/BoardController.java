package br.com.senior.burger_place.controller;

import br.com.senior.burger_place.domain.board.Board;
import br.com.senior.burger_place.domain.board.BoardLocation;
import br.com.senior.burger_place.domain.board.BoardService;
import br.com.senior.burger_place.domain.board.dto.BoardRegisterDTO;
import br.com.senior.burger_place.domain.board.dto.BoardUpdateDTO;
import br.com.senior.burger_place.domain.board.dto.ListingBoardDTO;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/boards")
public class BoardController {

    @Autowired
    private BoardService service;

    @PostMapping
    @Transactional
    public ResponseEntity<Object> register(@RequestBody @Valid BoardRegisterDTO dto, UriComponentsBuilder uriBuilder) {
        Board board = service.addBoard(dto);
        var uri = uriBuilder.path("/boards/{id}").buildAndExpand(board.getId()).toUri();
        return ResponseEntity.created(uri).body(board);
    }

    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity updateBoard(
            @PathVariable
            Long id,
            @RequestBody
            @Valid
            BoardUpdateDTO dto
    ) {
        service.updateBoard(id, dto);
        Board board = service.listBoardsById(id);
        ListingBoardDTO updatedData = new ListingBoardDTO(board);
        return ResponseEntity.status(HttpStatus.OK).body(updatedData);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> listBoardById(@PathVariable Long id) {
        Board board = service.listBoardsById(id);
        return ResponseEntity.ok(new ListingBoardDTO(board));
    }

    @GetMapping()
    public ResponseEntity<Page<ListingBoardDTO>> listBoards(
            @RequestParam(required = false) String location,
            @RequestParam(required = false) Integer capacity,
            Pageable pageable) {

        if ((location == null && capacity == null)) {
            Page<Board> boards = service.listAllBoards(pageable);
            return ResponseEntity.ok().body(boards.map(ListingBoardDTO::new));
        }
        if (capacity != null && location == null) {
            Page<Board> boards = service.listAvailableBoardsByCapacityAndOccupation(capacity, pageable);
            return ResponseEntity.ok().body(boards.map(ListingBoardDTO::new));
        }
        if (capacity == null && location != null) {
            try {
                BoardLocation boardLocation = BoardLocation.valueOf(location.toUpperCase());
                Page<Board> boards = service.listAvailableBoardsByLocationAndOccupation(boardLocation, pageable);
                return ResponseEntity.ok().body(boards.map(ListingBoardDTO::new));
            } catch (IllegalArgumentException e) {
                throw new EntityNotFoundException("Não encontrado mesa para a localização: " + location);
            }
        }
        try {
            BoardLocation boardLocation = BoardLocation.valueOf(location.toUpperCase());
            Page<Board> boards = service.listAvailableBoardsByLocationAndCapacityAndOccupation(boardLocation, capacity, pageable);
            return ResponseEntity.ok().body(boards.map(ListingBoardDTO::new));
        } catch (IllegalArgumentException e) {
            throw new EntityNotFoundException("Não encontrado mesa para a localização: " + location);
        }
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<Object> deleteBoard(@PathVariable Long id) {
        Board board = service.listBoardsById(id);
        board.inactivate();
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
