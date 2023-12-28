package br.com.senior.burger_place.controller;

import br.com.senior.burger_place.domain.board.Board;
import br.com.senior.burger_place.domain.board.BoardLocation;
import br.com.senior.burger_place.domain.occupation.Occupation;
import br.com.senior.burger_place.domain.occupation.OccupationService;
import br.com.senior.burger_place.domain.occupation.PaymentForm;
import br.com.senior.burger_place.domain.occupation.dto.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;
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

import java.time.LocalDateTime;
import java.util.*;

@WebMvcTest(controllers = {OccupationController.class})
@ExtendWith(MockitoExtension.class)
public class OccupationControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private OccupationController occupationController;
    @MockBean
    private OccupationService occupationService;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void listOccupations_whenExistOccupations_shouldReturnStatus200WithOccupations() throws Exception {
        Occupation occupation1 = new Occupation(
                1L,
                LocalDateTime.now(),
                null,
                2,
                PaymentForm.CARTAO_CREDITO,
                new ArrayList<>(),
                new Board(),
                new HashSet<>(),
                true
        );

        Occupation occupation2 = new Occupation(
                1L,
                LocalDateTime.now(),
                null,
                2,
                PaymentForm.CARTAO_CREDITO,
                new ArrayList<>(),
                new Board(),
                new HashSet<>(),
                true
        );

        PageImpl<ListOccupationDTO> somePage = new PageImpl<>(
                Arrays.asList(
                        new ListOccupationDTO(occupation1),
                        new ListOccupationDTO(occupation2)
                ),
                Pageable.ofSize(10),
                10
        );

        Mockito.when(this.occupationService.listOccupations(Mockito.any(Pageable.class))).thenReturn(somePage);

        ResultActions response = this.mockMvc
                .perform(
                        MockMvcRequestBuilders.get("/occupations")
                                .contentType(MediaType.APPLICATION_JSON)
                );

        response
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content.size()", CoreMatchers.is(2)))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    void listOccupations_whenOccupationsDoNotExist_shouldReturnStatus200WithOccupations() throws Exception {
        PageImpl<ListOccupationDTO> somePage = new PageImpl<>(
                List.of(),
                Pageable.ofSize(10),
                10
        );

        Mockito.when(this.occupationService.listOccupations(Mockito.any(Pageable.class))).thenReturn(somePage);

        ResultActions response = this.mockMvc
                .perform(
                        MockMvcRequestBuilders.get("/occupations")
                                .contentType(MediaType.APPLICATION_JSON)
                );

        response
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content.size()", CoreMatchers.is(0)))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    void showOccupation_whenOccupationExists_shouldReturnStatus200WithOccupation() throws Exception {
        OccupationDTO someOccupationDTO = new OccupationDTO(
                1L,
                LocalDateTime.now(),
                null,
                null,
                2,
                new OccupationBoardDTO(1, BoardLocation.AREA_INTERNA),
                List.of(),
                List.of()
        );

        Mockito.when(this.occupationService.showOccupation(Mockito.anyLong()))
                .thenReturn(Optional.of(someOccupationDTO));

        ResultActions response = this.mockMvc
                .perform(
                        MockMvcRequestBuilders.get("/occupations/{id}", 1L)
                                .contentType(MediaType.APPLICATION_JSON)
                );

        response
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(result -> {
                    OccupationDTO responseResult = this.objectMapper.readValue(
                            result.getResponse().getContentAsString(),
                            OccupationDTO.class);

                    Assertions.assertEquals(responseResult, someOccupationDTO);
                })
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    void showOccupation_whenOccupationDoesNotExist_shouldReturnStatus404() throws Exception {
        Mockito.when(this.occupationService.showOccupation(Mockito.anyLong()))
                .thenReturn(Optional.empty());

        ResultActions response = this.mockMvc
                .perform(
                        MockMvcRequestBuilders.get("/occupations/{id}", 1L)
                                .contentType(MediaType.APPLICATION_JSON)
                );

        response
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(result -> {
                    Assertions.assertTrue(result.getResponse().getContentAsString().isEmpty());
                })
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    void createOccupation_whenDTOHasAllFieldsNull_shouldReturnStatus400() throws Exception {
        CreateOccupationDTO input = new CreateOccupationDTO(null, null, null, null);

        ResultActions response = this.mockMvc
                .perform(
                        MockMvcRequestBuilders.post("/occupations")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(this.objectMapper.writeValueAsString(input))
                );

        response
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$.size()",
                        CoreMatchers.is(3))
                )
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$[0].field",
                        CoreMatchers.is("beginOccupation"))
                )
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$[0].message",
                        CoreMatchers.is("must not be null"))
                )
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$[1].field",
                        CoreMatchers.is("boardId"))
                )
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$[1].message",
                        CoreMatchers.is("must not be null"))
                )
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$[2].field",
                        CoreMatchers.is("peopleCount"))
                )
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$[2].message",
                        CoreMatchers.is("must not be null"))
                )
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    void createOccupation_whenDTOBeginOccupationIsNull_shouldReturnStatus400() throws Exception {
        CreateOccupationDTO input = new CreateOccupationDTO(null, 2, 2L, null);

        ResultActions response = this.mockMvc
                .perform(
                        MockMvcRequestBuilders.post("/occupations")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(this.objectMapper.writeValueAsString(input))
                );

        response
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$.size()",
                        CoreMatchers.is(1))
                )
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$[0].field",
                        CoreMatchers.is("beginOccupation"))
                )
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$[0].message",
                        CoreMatchers.is("must not be null"))
                )
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    void createOccupation_whenDTOBeginOccupationIsInFuture_shouldReturnStatus400() throws Exception {
        CreateOccupationDTO input = new CreateOccupationDTO(
                LocalDateTime.now().plusMinutes(10),
                2,
                2L,
                null
        );

        ResultActions response = this.mockMvc
                .perform(
                        MockMvcRequestBuilders.post("/occupations")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(this.objectMapper.writeValueAsString(input))
                );

        response
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$.size()",
                        CoreMatchers.is(1))
                )
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$[0].field",
                        CoreMatchers.is("beginOccupation"))
                )
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$[0].message",
                        CoreMatchers.is("must be a date in the past or in the present"))
                )
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    void createOccupation_whenDTOPeopleCountIsNull_shouldReturnStatus400() throws Exception {
        CreateOccupationDTO input = new CreateOccupationDTO(
                LocalDateTime.now(),
                null,
                2L,
                null
        );

        ResultActions response = this.mockMvc
                .perform(
                        MockMvcRequestBuilders.post("/occupations")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(this.objectMapper.writeValueAsString(input))
                );

        response
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$.size()",
                        CoreMatchers.is(1))
                )
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$[0].field",
                        CoreMatchers.is("peopleCount"))
                )
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$[0].message",
                        CoreMatchers.is("must not be null"))
                )
                .andDo(MockMvcResultHandlers.print());
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1, -10})
    void createOccupation_whenDTOPeopleCountIsNegativeOrZero_shouldReturnStatus400(Integer peopleCount) throws Exception {
        CreateOccupationDTO input = new CreateOccupationDTO(
                LocalDateTime.now(),
                peopleCount,
                2L,
                null
        );

        ResultActions response = this.mockMvc
                .perform(
                        MockMvcRequestBuilders.post("/occupations")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(this.objectMapper.writeValueAsString(input))
                );

        response
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$.size()",
                        CoreMatchers.is(1))
                )
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$[0].field",
                        CoreMatchers.is("peopleCount"))
                )
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$[0].message",
                        CoreMatchers.is("must be greater than 0"))
                )
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    void createOccupation_whenDTOBoardIdIsNull_shouldReturnStatus400() throws Exception {
        CreateOccupationDTO input = new CreateOccupationDTO(
                LocalDateTime.now(),
                2,
                null,
                null
        );

        ResultActions response = this.mockMvc
                .perform(MockMvcRequestBuilders.post("/occupations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.objectMapper.writeValueAsString(input))
                );

        response
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$.size()",
                        CoreMatchers.is(1))
                )
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$[0].field",
                        CoreMatchers.is("boardId"))
                )
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$[0].message",
                        CoreMatchers.is("must not be null"))
                )
                .andDo(MockMvcResultHandlers.print());
    }

    @ParameterizedTest
    @ValueSource(longs = {0L, -1L, -10L})
    void createOccupation_whenDTOBoardIdIsNegativeOrZero_shouldReturnStatus400(Long boardId) throws Exception {
        CreateOccupationDTO input = new CreateOccupationDTO(
                LocalDateTime.now(),
                2,
                boardId,
                null
        );

        ResultActions response = this.mockMvc
                .perform(
                        MockMvcRequestBuilders.post("/occupations")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(this.objectMapper.writeValueAsString(input))
                );

        response
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$.size()",
                        CoreMatchers.is(1))
                )
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$[0].field",
                        CoreMatchers.is("boardId"))
                )
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$[0].message",
                        CoreMatchers.is("must be greater than 0"))
                )
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    void createOccupation_whenDTOIsValid_shouldReturnStatus201() throws Exception {
        LocalDateTime someBeginOccupation = LocalDateTime.now();
        int somePeopleCount = 2;
        long someOccupationId = 1L;
        int someBoardNumber = 1;
        BoardLocation someBoardLocation = BoardLocation.AREA_INTERNA;

        CreateOccupationDTO input = new CreateOccupationDTO(
                someBeginOccupation,
                somePeopleCount,
                2L,
                null
        );

        OccupationDTO someOccupationDTO = new OccupationDTO(
                someOccupationId,
                someBeginOccupation,
                null,
                null,
                somePeopleCount,
                new OccupationBoardDTO(someBoardNumber, someBoardLocation),
                new ArrayList<>(),
                new ArrayList<>()
        );

        Mockito.when(this.occupationService.createOccupation(Mockito.any(CreateOccupationDTO.class)))
                .thenReturn(someOccupationDTO);

        ResultActions response = this.mockMvc
                .perform(
                        MockMvcRequestBuilders.post("/occupations")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(this.objectMapper.writeValueAsString(input))
                );

        String expectedLocation = String.format("http://localhost/occupations/%d", someOccupationId);

        response
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(result -> {
                    Assertions.assertTrue(result.getResponse().containsHeader("Location"));
                })
                .andExpect(result -> {
                    Assertions.assertEquals(
                            expectedLocation,
                            result.getResponse().getHeader("Location")
                    );
                })
                .andExpect(result -> {
                    Assertions.assertEquals(
                            expectedLocation,
                            result.getResponse().getRedirectedUrl()
                    );
                })
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$.id",
                        CoreMatchers.is(someOccupationId),
                        Long.class
                ))
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$.beginOccupation",
                        CoreMatchers.containsString(someBeginOccupation.toString())
                ))
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$.endOccupation",
                        CoreMatchers.nullValue()
                ))
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$.paymentForm",
                        CoreMatchers.nullValue()
                ))
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$.peopleCount",
                        CoreMatchers.is(somePeopleCount)
                ))
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$.board.number",
                        CoreMatchers.is(someBoardNumber)
                ))
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$.board.location",
                        CoreMatchers.is(someBoardLocation.name())
                ))
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$.orderItems.size()",
                        CoreMatchers.is(0)
                ))
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$.customers.size()",
                        CoreMatchers.is(0)
                ))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    void addOrderItems_whenDTOOrderItemsIsEmpty_shouldReturnStatus400() throws Exception {
        AddOrderItemsDTO input = new AddOrderItemsDTO(List.of());

        ResultActions response = this.mockMvc
                .perform(
                        MockMvcRequestBuilders.post("/occupations/{occupationId}/items", 1L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(this.objectMapper.writeValueAsString(input))
                );

        response
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$[0].field",
                        CoreMatchers.is("orderItems"))
                )
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$[0].message",
                        CoreMatchers.is("must not be empty"))
                )
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    void addOrderItems_whenDTOOrderItemsIsNull_shouldReturnStatus400() throws Exception {
        AddOrderItemsDTO input = new AddOrderItemsDTO(null);

        ResultActions response = this.mockMvc
                .perform(
                        MockMvcRequestBuilders.post("/occupations/{occupationId}/items", 1L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(this.objectMapper.writeValueAsString(input))
                );

        response
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$[0].field",
                        CoreMatchers.is("orderItems"))
                )
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$[0].message",
                        CoreMatchers.is("must not be empty"))
                )
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    void addOrderItems_whenDTOIsNull_shouldReturnStatus200() throws Exception {
        ResultActions response = this.mockMvc
                .perform(
                        MockMvcRequestBuilders.post("/occupations/{occupationId}/items", 1L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(this.objectMapper.writeValueAsString(null))
                );

        response
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    void addOrderItems_whenDTOIsValid_shouldReturnStatus200() throws Exception {
        AddOrderItemsDTO input = new AddOrderItemsDTO(
                Arrays.asList(
                        new CreateOrderItemDTO(1L, 2, null),
                        new CreateOrderItemDTO(2L, 2, null)
                )
        );

        ResultActions response = this.mockMvc
                .perform(
                        MockMvcRequestBuilders.post("/occupations/{occupationId}/items", 1L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(this.objectMapper.writeValueAsString(input))
                );

        response
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    void addOrderItems_whenDTOHasProductIdNull_shouldReturnStatus400() throws Exception {
        AddOrderItemsDTO input = new AddOrderItemsDTO(
                Arrays.asList(
                        new CreateOrderItemDTO(1L, 2, null),
                        new CreateOrderItemDTO(null, 2, null)
                )
        );

        ResultActions response = this.mockMvc
                .perform(
                        MockMvcRequestBuilders.post("/occupations/{occupationId}/items", 1L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(this.objectMapper.writeValueAsString(input))
                );

        response
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$.size()",
                        CoreMatchers.is(1))
                )
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$[0].field",
                        CoreMatchers.is("orderItems[1].productId"))
                )
                .andExpect(MockMvcResultMatchers.jsonPath(
                                "$[0].message",
                                CoreMatchers.is("must not be null")
                        )
                )
                .andDo(MockMvcResultHandlers.print());
    }

    @ParameterizedTest
    @ValueSource(longs = {0L, -1L, -10L})
    void addOrderItems_whenDTOHasInvalidProductId_shouldReturnStatus400(Long productId) throws Exception {
        AddOrderItemsDTO input = new AddOrderItemsDTO(
                Arrays.asList(
                        new CreateOrderItemDTO(1L, 2, null),
                        new CreateOrderItemDTO(productId, 2, null)
                )
        );

        ResultActions response = this.mockMvc
                .perform(
                        MockMvcRequestBuilders.post("/occupations/{occupationId}/items", 1L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(this.objectMapper.writeValueAsString(input))
                );

        response
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$.size()",
                        CoreMatchers.is(1))
                )
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$[0].field",
                        CoreMatchers.is("orderItems[1].productId"))
                )
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$[0].message",
                        CoreMatchers.is("must be greater than 0"))
                )
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    void addOrderItems_whenDTOHasAmountNull_shouldReturnStatus400() throws Exception {
        AddOrderItemsDTO input = new AddOrderItemsDTO(
                Arrays.asList(
                        new CreateOrderItemDTO(1L, 2, null),
                        new CreateOrderItemDTO(2L, null, null)
                )
        );

        ResultActions response = this.mockMvc
                .perform(
                        MockMvcRequestBuilders.post("/occupations/{occupationId}/items", 1L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(this.objectMapper.writeValueAsString(input))
                );

        response
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$.size()",
                        CoreMatchers.is(1))
                )
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$[0].field",
                        CoreMatchers.is("orderItems[1].amount"))
                )
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$[0].message",
                        CoreMatchers.is("must not be null"))
                )
                .andDo(MockMvcResultHandlers.print());
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1, -10})
    void addOrderItems_whenDTOHasInvalidAmount_shouldReturnStatus400(Integer amount) throws Exception {
        AddOrderItemsDTO input = new AddOrderItemsDTO(
                Arrays.asList(
                        new CreateOrderItemDTO(1L, 2, null),
                        new CreateOrderItemDTO(2L, amount, null)
                )
        );

        ResultActions response = this.mockMvc
                .perform(
                        MockMvcRequestBuilders.post("/occupations/{occupationId}/items", 1L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(this.objectMapper.writeValueAsString(input))
                );

        response
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$.size()",
                        CoreMatchers.is(1))
                )
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$[0].field",
                        CoreMatchers.is("orderItems[1].amount"))
                )
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$[0].message",
                        CoreMatchers.is("must be greater than 0"))
                )
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    void removeOrderItems_whenDTOOrderItemIdsIsEmpty_shouldReturn400() throws Exception {
        RemoveOrderItemsDTO input = new RemoveOrderItemsDTO(List.of());

        ResultActions response = this.mockMvc
                .perform(
                        MockMvcRequestBuilders.delete("/occupations/{occupationId}/items", 1L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(this.objectMapper.writeValueAsString(input))
                );

        response
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$[0].field",
                        CoreMatchers.is("orderItems"))
                )
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$[0].message",
                        CoreMatchers.is("must not be empty"))
                )
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    void removeOrderItems_whenDTOOrderItemIdsIsValid_shouldReturn204() throws Exception {
        RemoveOrderItemsDTO input = new RemoveOrderItemsDTO(List.of(1L, 2L));

        ResultActions response = this.mockMvc
                .perform(
                        MockMvcRequestBuilders.delete("/occupations/{occupationId}/items", 1L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(this.objectMapper.writeValueAsString(input))
                );

        response
                .andExpect(MockMvcResultMatchers.status().isNoContent())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    void updateOrder_whenDTOAmountIsNull_shouldReturnStatus400() throws Exception {
        UpdateOrderItemDTO input = new UpdateOrderItemDTO(null, null);

        ResultActions response = this.mockMvc
                .perform(
                        MockMvcRequestBuilders.put("/occupations/{occupationId}/items/{itemId}", 1L, 1L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(this.objectMapper.writeValueAsString(input))
                );

        response
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$[0].field",
                        CoreMatchers.is("amount"))
                )
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$[0].message",
                        CoreMatchers.is("must not be null"))
                )
                .andDo(MockMvcResultHandlers.print());
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1, -10})
    void updateOrder_whenDTOAmountIsNull_shouldReturnStatus400(Integer amount) throws Exception {
        UpdateOrderItemDTO input = new UpdateOrderItemDTO(amount, null);

        ResultActions response = this.mockMvc
                .perform(
                        MockMvcRequestBuilders.put("/occupations/{occupationId}/items/{itemId}", 1L, 1L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(this.objectMapper.writeValueAsString(input))
                );

        response
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$[0].field",
                        CoreMatchers.is("amount"))
                )
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$[0].message",
                        CoreMatchers.is("must be greater than 0"))
                )
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    void updateOrder_whenDTOIsValid_shouldReturnStatus204() throws Exception {
        UpdateOrderItemDTO input = new UpdateOrderItemDTO(2, null);

        ResultActions response = this.mockMvc
                .perform(
                        MockMvcRequestBuilders.put("/occupations/{occupationId}/items/{itemId}", 1L, 1L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(this.objectMapper.writeValueAsString(input))
                );

        response
                .andExpect(MockMvcResultMatchers.status().isNoContent())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    void inactivateOrder_whenIsValid_shouldReturnStatus204() throws Exception {
        ResultActions response = this.mockMvc
                .perform(
                        MockMvcRequestBuilders.delete("/occupations/{occupationId}", 1L)
                                .contentType(MediaType.APPLICATION_JSON)
                );

        response
                .andExpect(MockMvcResultMatchers.status().isNoContent())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    void startOrderItemPreparation_whenIsValid_shouldReturnStatus204() throws Exception {
        ResultActions response = this.mockMvc
                .perform(
                        MockMvcRequestBuilders.patch("/occupations/{occupationId}/items/{itemId}/start-preparation", 1L, 1L)
                                .contentType(MediaType.APPLICATION_JSON)
                );

        response
                .andExpect(MockMvcResultMatchers.status().isNoContent())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    void finishOrderItemPreparation_whenIsValid_shouldReturnStatus204() throws Exception {
        ResultActions response = this.mockMvc
                .perform(
                        MockMvcRequestBuilders.patch("/occupations/{occupationId}/items/{itemId}/finish-preparation", 1L, 1L)
                                .contentType(MediaType.APPLICATION_JSON)
                );

        response
                .andExpect(MockMvcResultMatchers.status().isNoContent())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    void deliverOrderItemPreparation_whenIsValid_shouldReturnStatus204() throws Exception {
        ResultActions response = this.mockMvc
                .perform(
                        MockMvcRequestBuilders.patch("/occupations/{occupationId}/items/{itemId}/deliver", 1L, 1L)
                                .contentType(MediaType.APPLICATION_JSON)
                );

        response
                .andExpect(MockMvcResultMatchers.status().isNoContent())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    void cancelOrderItem_whenIsValid_shouldReturnStatus204() throws Exception {
        ResultActions response = this.mockMvc
                .perform(
                        MockMvcRequestBuilders.patch("/occupations/{occupationId}/items/{itemId}/cancel", 1L, 1L)
                                .contentType(MediaType.APPLICATION_JSON)
                );

        response
                .andExpect(MockMvcResultMatchers.status().isNoContent())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    void finishOccupation_whenIsValid_shouldReturnStatus204() throws Exception {
        FinishOccupationDTO input = new FinishOccupationDTO(
                LocalDateTime.now().plusMinutes(1),
                PaymentForm.CARTAO_CREDITO
        );

        ResultActions response = this.mockMvc
                .perform(
                        MockMvcRequestBuilders.patch("/occupations/{occupationId}/finish", 1L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(this.objectMapper.writeValueAsString(input))
                );

        response
                .andExpect(MockMvcResultMatchers.status().isNoContent())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    void finishOccupation_whenDTOIsNull_shouldReturnStatus204() throws Exception {
        ResultActions response = this.mockMvc
                .perform(
                        MockMvcRequestBuilders.patch("/occupations/{occupationId}/finish", 1L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(this.objectMapper.writeValueAsString(null))
                );

        response
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    void finishOccupation_whenDTOEndOccupationIsNull_shouldReturnStatus204() throws Exception {
        FinishOccupationDTO input = new FinishOccupationDTO(
                null,
                PaymentForm.CARTAO_CREDITO
        );

        ResultActions response = this.mockMvc
                .perform(
                        MockMvcRequestBuilders.patch("/occupations/{occupationId}/finish", 1L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(this.objectMapper.writeValueAsString(input))
                );

        response
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$[0].field",
                        CoreMatchers.is("endOccupation"))
                )
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$[0].message",
                        CoreMatchers.is("must not be null"))
                )
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    void finishOccupation_whenDTOEndOccupationIsInPass_shouldReturnStatus204() throws Exception {
        FinishOccupationDTO input = new FinishOccupationDTO(
                LocalDateTime.now().minusMinutes(10),
                PaymentForm.CARTAO_CREDITO
        );

        ResultActions response = this.mockMvc
                .perform(
                        MockMvcRequestBuilders.patch("/occupations/{occupationId}/finish", 1L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(this.objectMapper.writeValueAsString(input))
                );

        response
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$[0].field",
                        CoreMatchers.is("endOccupation"))
                )
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$[0].message",
                        CoreMatchers.is("must be a date in the present or in the future"))
                )
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    void finishOccupation_whenDTOPaymentFormIsNull_shouldReturnStatus204() throws Exception {
        FinishOccupationDTO input = new FinishOccupationDTO(
                LocalDateTime.now().plusMinutes(10),
                null
        );

        ResultActions response = this.mockMvc
                .perform(
                        MockMvcRequestBuilders.patch("/occupations/{occupationId}/finish", 1L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(this.objectMapper.writeValueAsString(input))
                );

        response
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$[0].field",
                        CoreMatchers.is("paymentForm"))
                )
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$[0].message",
                        CoreMatchers.is("must not be null"))
                )
                .andDo(MockMvcResultHandlers.print());
    }
}
