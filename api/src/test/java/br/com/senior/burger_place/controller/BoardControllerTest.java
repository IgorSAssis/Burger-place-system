package br.com.senior.burger_place.controller;

import br.com.senior.burger_place.domain.board.Board;
import br.com.senior.burger_place.domain.board.BoardConverter;
import br.com.senior.burger_place.domain.board.BoardLocation;
import br.com.senior.burger_place.domain.board.BoardService;
import br.com.senior.burger_place.domain.board.dto.BoardDTO;
import br.com.senior.burger_place.domain.board.dto.CreateBoardDTO;
import br.com.senior.burger_place.domain.board.dto.UpdateBoardDTO;
import br.com.senior.burger_place.infra.dto.ResponseWithFieldErrors;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static utils.BoardCreator.*;

@WebMvcTest(controllers = {BoardController.class})
@ExtendWith(MockitoExtension.class)
@DisplayName("BoardController integration tests")
class BoardControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private BoardController boardController;
    @MockBean
    private BoardService boardService;
    @MockBean
    private BoardConverter boardConverter;
    @Autowired
    private ObjectMapper objectMapper;

    @Nested
    @DisplayName("list tests")
    class ListTest {
        @Test
        void list_whenExistBoards_shouldReturnStatus200AndPageWithBoardDTO() throws Exception {
            List<Board> boards = List.of(createBoard(), createBoard());
            Page<Board> boardPage = new PageImpl<>(boards, Pageable.ofSize(20), 20);

            mockListBoards(boardPage);
            mockToBoardDTO(boards.get(0), createBoardDTO());
            mockToBoardDTO(boards.get(1), createBoardDTO());

            ResultActions response = mockMvc.perform(MockMvcRequestBuilders.get("/boards")
                    .contentType(MediaType.APPLICATION_JSON)
                    .queryParam("number", "1")
                    .queryParam("capacity", "1")
                    .queryParam("location", BoardLocation.TERRACO.name())
                    .queryParam("active", "true")
                    .queryParam("occupied", "true"));

            response
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.jsonPath(
                            "$.content.size()",
                            CoreMatchers.is(boards.size()))
                    )
                    .andDo(MockMvcResultHandlers.print());
        }

        @Test
        void list_whenBoardsDoNotExist_shouldReturnStatus200AndEmptyPage() throws Exception {
            Page<Board> boardPage = new PageImpl<>(List.of(), Pageable.ofSize(20), 20);

            mockListBoards(boardPage);

            ResultActions response = mockMvc.perform(MockMvcRequestBuilders.get("/boards")
                    .contentType(MediaType.APPLICATION_JSON)
                    .queryParam("number", "1")
                    .queryParam("capacity", "1")
                    .queryParam("location", BoardLocation.TERRACO.name())
                    .queryParam("active", "true")
                    .queryParam("occupied", "true"));

            response
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.jsonPath(
                            "$.content.size()",
                            CoreMatchers.is(boardPage.getContent().size()))
                    )
                    .andDo(MockMvcResultHandlers.print());
        }

        private void mockListBoards(Page<Board> expectedReturn) {
            Mockito.when(boardService.listBoards(
                    Pageable.ofSize(20),
                    1,
                    1,
                    BoardLocation.TERRACO,
                    true,
                    true
            )).thenReturn(expectedReturn);
        }
    }

    @Nested
    @DisplayName("show tests")
    class ShowTest {
        @Test
        void show_whenBoardExists_shouldReturnStatus200AndBoardDTO() throws Exception {
            Board board = createBoard();
            BoardDTO boardDTO = createBoardDTO();
            boardDTO.setId(board.getId());

            Mockito.when(boardService.showBoard(Mockito.any(UUID.class))).thenReturn(board);
            mockToBoardDTO(board, boardDTO);

            ResultActions response = mockMvc.perform(
                    MockMvcRequestBuilders.get("/boards/{id}", board.getId())
                            .contentType(MediaType.APPLICATION_JSON));

            response
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(result -> {

                        BoardDTO output = objectMapper.readValue(
                                result.getResponse().getContentAsString(),
                                BoardDTO.class
                        );

                        assertEquals(boardDTO, output);
                    })
                    .andDo(MockMvcResultHandlers.print());
        }
    }

    @Nested
    @DisplayName("create tests")
    class CreateTest {
        @ParameterizedTest
        @CsvSource({
                ",Board number cannot be null",
                "0,Board number cannot be zero or negative",
                "-1,Board number cannot be zero or negative",
                "-10,Board number cannot be zero or negative",
        })
        void create_whenDTOBoardNumberIsInvalid_shouldReturnStatus400WithError(Integer boardNumber, String expectedErrorMessage) throws Exception {
            String field = "boardNumber";
            CreateBoardDTO createBoardDTO = createCreateBoardDTO();
            createBoardDTO.setBoardNumber(boardNumber);

            ResultActions response = mockMvc.perform(
                    MockMvcRequestBuilders.post("/boards")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsBytes(createBoardDTO))
            );

            response
                    .andExpect(MockMvcResultMatchers.status().isBadRequest())
                    .andExpect(result -> {
                        ResponseWithFieldErrors output = objectMapper.readValue(
                                result.getResponse().getContentAsString(),
                                ResponseWithFieldErrors.class
                        );

                        assertAll(
                                () -> assertNotNull(output),
                                () -> assertEquals(1, output.getErrors().size()),
                                () -> assertEquals(field, output.getErrors().get(0).getField()),
                                () -> assertEquals(expectedErrorMessage, output.getErrors().get(0).getMessage())
                        );
                    })
                    .andDo(MockMvcResultHandlers.print());
        }

        @ParameterizedTest
        @CsvSource({
                ",Board capacity cannot be null",
                "0,Board capacity cannot be zero or negative",
                "-1,Board capacity cannot be zero or negative",
                "-10,Board capacity cannot be zero or negative",
        })
        void create_whenDTOCapacityIsInvalid_shouldReturnStatus400WithError(Integer capacity, String expectedErrorMessage) throws Exception {
            String field = "capacity";
            CreateBoardDTO createBoardDTO = createCreateBoardDTO();
            createBoardDTO.setCapacity(capacity);

            ResultActions response = mockMvc.perform(
                    MockMvcRequestBuilders.post("/boards")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsBytes(createBoardDTO))
            );

            response
                    .andExpect(MockMvcResultMatchers.status().isBadRequest())
                    .andExpect(result -> {
                        ResponseWithFieldErrors output = objectMapper.readValue(
                                result.getResponse().getContentAsString(),
                                ResponseWithFieldErrors.class
                        );

                        assertAll(
                                () -> assertNotNull(output),
                                () -> assertEquals(1, output.getErrors().size()),
                                () -> assertEquals(field, output.getErrors().get(0).getField()),
                                () -> assertEquals(expectedErrorMessage, output.getErrors().get(0).getMessage())
                        );
                    })
                    .andDo(MockMvcResultHandlers.print());
        }

        @Test
        void create_whenDTOBoardLocationIsInvalid_shouldReturnStatus400WithError() throws Exception {
            String field = "boardLocation";
            String expectedErrorMessage = "Board location cannot be null";

            CreateBoardDTO createBoardDTO = createCreateBoardDTO();
            createBoardDTO.setBoardLocation(null);

            ResultActions response = mockMvc.perform(
                    MockMvcRequestBuilders.post("/boards")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsBytes(createBoardDTO))
            );

            response
                    .andExpect(MockMvcResultMatchers.status().isBadRequest())
                    .andExpect(result -> {
                        ResponseWithFieldErrors output = objectMapper.readValue(
                                result.getResponse().getContentAsString(),
                                ResponseWithFieldErrors.class
                        );

                        assertAll(
                                () -> assertNotNull(output),
                                () -> assertEquals(1, output.getErrors().size()),
                                () -> assertEquals(field, output.getErrors().get(0).getField()),
                                () -> assertEquals(expectedErrorMessage, output.getErrors().get(0).getMessage())
                        );
                    })
                    .andDo(MockMvcResultHandlers.print());
        }

        @Test
        void create_whenDTOIsValid_shouldReturnStatus201WithBoardDTOAndLocation() throws Exception {
            CreateBoardDTO createBoardDTO = createCreateBoardDTO();
            Board board = createBoard();
            BoardDTO boardDTO = createBoardDTO();
            boardDTO.setId(board.getId());

            String expectedCreatedResource = String.format("/boards/%s", board.getId().toString());

            Mockito.when(boardService.createBoard(Mockito.any(CreateBoardDTO.class))).thenReturn(board);
            mockToBoardDTO(board, boardDTO);

            ResultActions response = mockMvc.perform(
                    MockMvcRequestBuilders.post("/boards")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsBytes(createBoardDTO))
            );

            response
                    .andExpect(MockMvcResultMatchers.status().isCreated())
                    .andExpect(result -> {
                        BoardDTO output = objectMapper.readValue(result.getResponse().getContentAsString(), BoardDTO.class);

                        assertAll(
                                () -> assertNotNull(output),
                                () -> assertEquals(boardDTO, output),
                                () -> assertNotNull(result.getResponse().getRedirectedUrl()),
                                () -> assertTrue(result.getResponse().getRedirectedUrl().contains(expectedCreatedResource))
                        );
                    })
                    .andDo(MockMvcResultHandlers.print());
        }
    }

    @Nested
    @DisplayName("update tests")
    class UpdateTest {
        @ParameterizedTest
        @ValueSource(ints = {0, -1})
        void update_whenDTOBoardNumberIsNegativeOrZero_shouldReturnStatus400WithError(Integer boardNumber) throws Exception {
            String field = "boardNumber";
            String expectedErrorMessage = "Board number cannot be zero or negative";

            UpdateBoardDTO updateBoardDTO = createUpdateBoardDTO();
            updateBoardDTO.setBoardNumber(boardNumber);

            ResultActions response = mockMvc.perform(
                    MockMvcRequestBuilders.put("/boards/{id}", UUID.randomUUID().toString())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsBytes(updateBoardDTO))
            );

            response
                    .andExpect(MockMvcResultMatchers.status().isBadRequest())
                    .andExpect(result -> {
                        ResponseWithFieldErrors output = objectMapper.readValue(
                                result.getResponse().getContentAsString(),
                                ResponseWithFieldErrors.class
                        );

                        assertAll(
                                () -> assertNotNull(output),
                                () -> assertEquals(1, output.getErrors().size()),
                                () -> assertEquals(field, output.getErrors().get(0).getField()),
                                () -> assertEquals(expectedErrorMessage, output.getErrors().get(0).getMessage())
                        );
                    })
                    .andDo(MockMvcResultHandlers.print());
        }

        @ParameterizedTest
        @ValueSource(ints = {0, -1})
        void update_whenDTOCapacityIsNegativeOrZero_shouldReturnStatus400WithError(Integer capacity) throws Exception {
            String field = "capacity";
            String expectedErrorMessage = "Board capacity cannot be zero or negative";

            UpdateBoardDTO updateBoardDTO = createUpdateBoardDTO();
            updateBoardDTO.setCapacity(capacity);

            ResultActions response = mockMvc.perform(
                    MockMvcRequestBuilders.put("/boards/{id}", UUID.randomUUID().toString())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsBytes(updateBoardDTO))
            );

            response
                    .andExpect(MockMvcResultMatchers.status().isBadRequest())
                    .andExpect(result -> {
                        ResponseWithFieldErrors output = objectMapper.readValue(
                                result.getResponse().getContentAsString(),
                                ResponseWithFieldErrors.class
                        );

                        assertAll(
                                () -> assertNotNull(output),
                                () -> assertEquals(1, output.getErrors().size()),
                                () -> assertEquals(field, output.getErrors().get(0).getField()),
                                () -> assertEquals(expectedErrorMessage, output.getErrors().get(0).getMessage())
                        );
                    })
                    .andDo(MockMvcResultHandlers.print());
        }

        @Test
        void update_whenDTOIsValid_shouldReturnStatus200WithBoardDTO() throws Exception {
            UpdateBoardDTO updateBoardDTO = createUpdateBoardDTO();
            Board board = createBoard();
            BoardDTO boardDTO = createBoardDTO();
            boardDTO.setId(board.getId());

            Mockito.when(
                    boardService.updateBoard(Mockito.any(UUID.class), Mockito.any(UpdateBoardDTO.class))
            ).thenReturn(board);
            mockToBoardDTO(board, boardDTO);

            ResultActions response = mockMvc.perform(
                    MockMvcRequestBuilders.put("/boards/{id}", UUID.randomUUID().toString())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsBytes(updateBoardDTO))
            );

            response
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(result -> {
                        BoardDTO output = objectMapper.readValue(
                                result.getResponse().getContentAsString(),
                                BoardDTO.class
                        );

                        assertAll(
                                () -> assertNotNull(output),
                                () -> assertEquals(boardDTO, output)
                        );
                    })
                    .andDo(MockMvcResultHandlers.print());
        }
    }

    @Nested
    @DisplayName("inactivate tests")
    class InactivateTest {
        @Test
        void inactivate_whenCalled_shouldReturnStatus204() throws Exception {
            ResultActions response = mockMvc.perform(
                    MockMvcRequestBuilders.delete("/boards/{id}", UUID.randomUUID().toString())
                            .contentType(MediaType.APPLICATION_JSON)
            );

            response
                    .andExpect(MockMvcResultMatchers.status().isNoContent())
                    .andDo(MockMvcResultHandlers.print());
        }
    }

    private void mockToBoardDTO(Board board, BoardDTO expectedReturn) {
        Mockito.when(boardConverter.toBoardDTO(board)).thenReturn(expectedReturn);
    }
}