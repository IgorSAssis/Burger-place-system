package br.com.senior.burger_place.controller;

import br.com.senior.burger_place.domain.board.Board;
import br.com.senior.burger_place.domain.board.BoardService;
import br.com.senior.burger_place.domain.board.dto.BoardRegisterDTO;
import br.com.senior.burger_place.domain.board.dto.BoardUpdateDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Arrays;

import static br.com.senior.burger_place.domain.board.BoardLocation.TERRACO;
import static br.com.senior.burger_place.domain.board.BoardLocation.VARANDA;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(controllers = {BoardController.class})
@ExtendWith(MockitoExtension.class)
class BoardControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private BoardController boardController;
    @MockBean
    private BoardService boardService;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void register_whenRegistrationDataNotIsValid_shouldReturnHttpStatus400() throws Exception {
        BoardRegisterDTO dto = new BoardRegisterDTO(null, null, null);

        ResultActions result = this.mockMvc
                .perform(MockMvcRequestBuilders.post("/boards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.objectMapper.writeValueAsString(dto))
                );

        result
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(jsonPath(
                        "$[0].field",
                        CoreMatchers.is("capacity"))
                )
                .andExpect(jsonPath(
                        "$[0].message",
                        CoreMatchers.is("must not be null")
                ))
                .andExpect(jsonPath(
                        "$[1].field",
                        CoreMatchers.is("location"))
                )
                .andExpect(jsonPath(
                        "$[1].message",
                        CoreMatchers.is("must not be null")
                ))
                .andExpect(jsonPath(
                        "$[2].field",
                        CoreMatchers.is("number"))
                )
                .andExpect(jsonPath(
                        "$[2].message",
                        CoreMatchers.is("must not be null")
                ))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void register_whenCustomerRegistrationDTOIsValid_shouldReturnStatus201() throws Exception {
        BoardRegisterDTO dto = new BoardRegisterDTO(1, 3, VARANDA);

        Board board = new Board(dto);
        board.setId(1L);

        when(boardService.addBoard(dto)).thenReturn(board);

        ResultActions response = this.mockMvc
                .perform(MockMvcRequestBuilders.post("/boards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.objectMapper.writeValueAsString(dto))
                );
        String expectedLocation = String.format("http://localhost/boards/%d", 1L);

        response
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(result -> {
                    assertTrue(result.getResponse().containsHeader("Location"));
                })
                .andExpect(result -> {
                    Assertions.assertEquals(
                            expectedLocation,
                            result.getResponse().getHeader("Location")
                    );
                })
                .andExpect(result -> {
                    Assertions.assertEquals(expectedLocation,
                            result.getResponse().getRedirectedUrl());
                })
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$.id",
                        CoreMatchers.is(board.getId().intValue())
                ))
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$.number",
                        CoreMatchers.is(board.getNumber())
                ))
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$.capacity",
                        CoreMatchers.is(board.getCapacity())
                ))
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$.location",
                        CoreMatchers.is(board.getLocation().toString())
                ))
                .andDo(MockMvcResultHandlers.print());

        verify(boardService, times(1)).addBoard(dto);
    }

    @Test
    public void updateBoard_whenDtoIsValid_shouldReturnStatus200() throws Exception {
        Board board = new Board(1l, 3, 4, VARANDA, true);
        BoardUpdateDTO updatedDto = new BoardUpdateDTO(3, 6, VARANDA);

        when(boardService.listBoardsById(1l)).thenReturn(board);

        ResultActions response = this.mockMvc
                .perform(MockMvcRequestBuilders.put("/boards/{id}", 1l)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.objectMapper.writeValueAsString(updatedDto))
                );

        response
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());
        verify(boardService, times(1)).listBoardsById(1l);
        verify(boardService, times(1)).updateBoard(1l, updatedDto);


    }

    @Test
    public void listBoardById_whenExistBoard_shouldReturnStatus200WithBoard() throws Exception {
        Board board = new Board(1l, 7, 2, TERRACO, true);
        when(boardService.listBoardsById(1l)).thenReturn(board);

        ResultActions response = this.mockMvc
                .perform(MockMvcRequestBuilders.get("/boards/{id}", 1l)
                        .contentType(MediaType.APPLICATION_JSON)
                );
        response
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(result -> {
                    Board responseResult = this.objectMapper.readValue(
                            result.getResponse().getContentAsString(),
                            Board.class);

                    Assertions.assertEquals(board.getCapacity(), responseResult.getCapacity());
                    Assertions.assertEquals(board.getLocation(), responseResult.getLocation());
                    Assertions.assertEquals(board.getNumber(), responseResult.getNumber());
                })
                .andDo(MockMvcResultHandlers.print());
        verify(boardService, times(1)).listBoardsById(1l);
    }
    @Test
    public void listBoardById_whenBoardDoesNotExist_shouldReturnStatus404() throws Exception {
        when(boardService.listBoardsById(1l)).thenThrow(new EntityNotFoundException("Mesa não existe"));

        ResultActions response = this.mockMvc
                .perform(MockMvcRequestBuilders.get("/boards/{id}", 1l)
                        .contentType(MediaType.APPLICATION_JSON)
                );

        response
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(content().string(org.hamcrest.Matchers.not(org.hamcrest.Matchers.isEmptyOrNullString())))
                        .andDo(MockMvcResultHandlers.print());

                    verify(boardService, times(1)).listBoardsById(1l);
    }
    @Test
    public void listBoards_whenExistBoards_shouldReturnStatus200WithAllBoards() throws Exception {
        Board board1 = new Board(new BoardRegisterDTO(1, 3, VARANDA));
        Board board2 = new Board(new BoardRegisterDTO(2, 4, TERRACO));

        PageImpl<Board> somePage = new PageImpl<>(
                Arrays.asList(
                        board1,
                        board2
                ),
                Pageable.ofSize(10), 10);

        when(boardService.listAllBoards(any(Pageable.class))).thenReturn(somePage);

        ResultActions response = this.mockMvc
                .perform(MockMvcRequestBuilders.get("/boards")
                        .contentType(MediaType.APPLICATION_JSON)
                );
        response
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.content.size()", CoreMatchers.is(2)))
                .andDo(MockMvcResultHandlers.print());
        verify(boardService, times(1)).listAllBoards(any(Pageable.class));
        verify(boardService, never()).listAvailableBoardsByCapacityAndOccupation(any(), any(Pageable.class));
        verify(boardService, never()).listAvailableBoardsByLocationAndOccupation(any(), any(Pageable.class));
        verify(boardService, never()).listAvailableBoardsByLocationAndCapacityAndOccupation(any(), any(), any(Pageable.class));
    }

    @Test
    public void listBoards_whenExistBoards_shouldReturnStatus200WithAllBoardsWithCapacity4() throws Exception {
        Board board1 = new Board(new BoardRegisterDTO(1, 4, VARANDA));
        Board board2 = new Board(new BoardRegisterDTO(2, 4, TERRACO));

        PageImpl<Board> somePage = new PageImpl<>(
                Arrays.asList(
                        board1,
                        board2
                ),
                Pageable.ofSize(10), 10);

        when(boardService.listAvailableBoardsByCapacityAndOccupation(eq(4), any(Pageable.class))).thenReturn(somePage);

        ResultActions response = this.mockMvc
                .perform(MockMvcRequestBuilders.get("/boards")
                        .param("capacity", "4")
                        .contentType(MediaType.APPLICATION_JSON)
                );
        response
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.content.size()", CoreMatchers.is(2)))
                .andDo(MockMvcResultHandlers.print());

        verify(boardService, times(1)).listAvailableBoardsByCapacityAndOccupation(eq(4), any(Pageable.class));
        verify(boardService, never()).listAllBoards(any(Pageable.class));
        verify(boardService, never()).listAvailableBoardsByLocationAndOccupation(any(), any(Pageable.class));
        verify(boardService, never()).listAvailableBoardsByLocationAndCapacityAndOccupation(any(), any(), any(Pageable.class));
    }

    @Test
    public void listBoards_whenExistBoards_shouldReturnStatus200WithAllBoardsWithLocationVARANDA() throws Exception {
        Board board1 = new Board(new BoardRegisterDTO(1, 4, VARANDA));
        Board board2 = new Board(new BoardRegisterDTO(2, 3, VARANDA));

        PageImpl<Board> somePage = new PageImpl<>(
                Arrays.asList(
                        board1,
                        board2
                ),
                Pageable.ofSize(10), 10);

        when(boardService.listAvailableBoardsByLocationAndOccupation(eq(VARANDA), any(Pageable.class))).thenReturn(somePage);

        ResultActions response = this.mockMvc
                .perform(MockMvcRequestBuilders.get("/boards")
                        .param("location", "varanda")
                        .contentType(MediaType.APPLICATION_JSON)
                );
        response
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.content.size()", CoreMatchers.is(2)))
                .andDo(MockMvcResultHandlers.print());

        verify(boardService, times(1)).listAvailableBoardsByLocationAndOccupation(eq(VARANDA), any(Pageable.class));
        verify(boardService, never()).listAvailableBoardsByCapacityAndOccupation(any(), any(Pageable.class));
        verify(boardService, never()).listAllBoards(any(Pageable.class));
        verify(boardService, never()).listAvailableBoardsByLocationAndCapacityAndOccupation(any(), any(), any(Pageable.class));
    }

    @Test
    public void listBoards_whenExistBoardsByLocationAndOccupationNotIsValid_shouldReturnStatus404() throws Exception {
        when(boardService.listAvailableBoardsByLocationAndOccupation(any(), any(Pageable.class))).thenThrow(IllegalArgumentException.class);

        this.mockMvc
                .perform(MockMvcRequestBuilders.get("/boards")
                        .param("location", "varanda")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof EntityNotFoundException));

        verify(boardService, times(1)).listAvailableBoardsByLocationAndOccupation(eq(VARANDA), any(Pageable.class));
        verify(boardService, never()).listAvailableBoardsByCapacityAndOccupation(any(), any(Pageable.class));
        verify(boardService, never()).listAllBoards(any(Pageable.class));
        verify(boardService, never()).listAvailableBoardsByLocationAndCapacityAndOccupation(any(), any(), any(Pageable.class));
    }

    @Test
    public void listBoards_whenExistBoards_shouldReturnStatus200WithAllBoardsWithLocationVARANDAAndCapacity4() throws Exception {
        Board board1 = new Board(new BoardRegisterDTO(1, 4, VARANDA));
        Board board2 = new Board(new BoardRegisterDTO(2, 4, VARANDA));

        PageImpl<Board> somePage = new PageImpl<>(
                Arrays.asList(
                        board1,
                        board2
                ),
                Pageable.ofSize(10), 10);

        when(boardService.listAvailableBoardsByLocationAndCapacityAndOccupation(eq(VARANDA), eq(4), any(Pageable.class))).thenReturn(somePage);

        ResultActions response = this.mockMvc
                .perform(MockMvcRequestBuilders.get("/boards")
                        .param("location", "varanda")
                        .param("capacity", "4")
                        .contentType(MediaType.APPLICATION_JSON)
                );
        response
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.content.size()", CoreMatchers.is(2)))
                .andDo(MockMvcResultHandlers.print());

        verify(boardService, times(1)).listAvailableBoardsByLocationAndCapacityAndOccupation(eq(VARANDA), eq(4), any(Pageable.class));
        verify(boardService, never()).listAvailableBoardsByLocationAndOccupation(any(), any(Pageable.class));
        verify(boardService, never()).listAvailableBoardsByCapacityAndOccupation(any(), any(Pageable.class));
        verify(boardService, never()).listAllBoards(any(Pageable.class));
    }

    @Test
    public void listBoards_whenExistBoardsByLocationAndCapacityAndOccupationNotIsValid_shouldReturnStatus404() throws Exception {
        when(boardService.listAvailableBoardsByLocationAndCapacityAndOccupation(any(), any(), any(Pageable.class))).thenThrow(IllegalArgumentException.class);

        this.mockMvc
                .perform(MockMvcRequestBuilders.get("/boards")
                        .param("location", "varanda")
                        .param("capacity", "4")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof EntityNotFoundException));

        verify(boardService, times(1)).listAvailableBoardsByLocationAndCapacityAndOccupation(any(), any(), any(Pageable.class));
        verify(boardService, never()).listAvailableBoardsByLocationAndOccupation(any(), any(Pageable.class));
        verify(boardService, never()).listAvailableBoardsByCapacityAndOccupation(any(), any(Pageable.class));
        verify(boardService, never()).listAllBoards(any(Pageable.class));
    }

    @Test
    public void deleteBoard_whenBoardExistsAndIsActive_sholdReturnStatus2004() throws Exception {
        Board board = new Board(1l, 4, 4, TERRACO, true);
        when(boardService.listBoardsById(1l)).thenReturn(board);

        ResultActions response = this.mockMvc
                .perform(MockMvcRequestBuilders.delete("/boards/{id}", 1l)
                        .contentType(MediaType.APPLICATION_JSON)
                );
        response
                .andExpect(MockMvcResultMatchers.status().isNoContent())
                .andDo(MockMvcResultHandlers.print());
        verify(boardService, times(1)).listBoardsById(1l);
        Assertions.assertFalse(board.isActive());
    }

}