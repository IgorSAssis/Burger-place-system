package br.com.senior.burger_place.controller;

import br.com.senior.burger_place.domain.board.BoardConverter;
import br.com.senior.burger_place.domain.board.BoardLocation;
import br.com.senior.burger_place.domain.board.BoardService;
import br.com.senior.burger_place.domain.board.dto.BoardDTO;
import br.com.senior.burger_place.domain.board.dto.CreateBoardDTO;
import br.com.senior.burger_place.domain.board.dto.UpdateBoardDTO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("boards")
public class BoardController {
    @Autowired
    private BoardService boardService;
    @Autowired
    private BoardConverter boardConverter;

    @GetMapping()
    public ResponseEntity<Page<BoardDTO>> list(
            Pageable pageable,
            @RequestParam(name = "number", required = false)
            Integer boardNumber,
            @RequestParam(name = "capacity", required = false)
            Integer capacity,
            @RequestParam(name = "location", required = false)
            BoardLocation location,
            @RequestParam(name = "active", required = false)
            Boolean active,
            @RequestParam(name = "occupied", required = false)
            Boolean occupied
    ) {
        Page<BoardDTO> boards = this.boardService.listBoards(
                pageable,
                boardNumber,
                capacity,
                location,
                active,
                occupied
        ).map(this.boardConverter::toBoardDTO);

        return ResponseEntity.ok(boards);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BoardDTO> show(
            @PathVariable
            UUID id
    ) {
        return ResponseEntity.ok(
                this.boardConverter.toBoardDTO(this.boardService.showBoard(id))
        );
    }

    @PostMapping
    public ResponseEntity<BoardDTO> create(
            @Valid
            @RequestBody
            CreateBoardDTO dto,
            UriComponentsBuilder uriBuilder
    ) {
        BoardDTO board = this.boardConverter.toBoardDTO(this.boardService.createBoard(dto));

        URI uri = uriBuilder
                .path("/boards/{id}")
                .buildAndExpand(board.getId())
                .toUri();

        return ResponseEntity.created(uri).body(board);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BoardDTO> update(
            @PathVariable
            UUID id,
            @Valid
            @RequestBody
            UpdateBoardDTO dto
    ) {
        return ResponseEntity.ok(
                this.boardConverter.toBoardDTO(boardService.updateBoard(id, dto))
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> inactivate(
            @PathVariable
            UUID id
    ) {
        this.boardService.inactivateBoard(id);

        return ResponseEntity.noContent().build();
    }
}
