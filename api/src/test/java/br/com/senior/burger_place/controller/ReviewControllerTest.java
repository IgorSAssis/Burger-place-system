package br.com.senior.burger_place.controller;

import br.com.senior.burger_place.domain.board.Board;
import br.com.senior.burger_place.domain.occupation.Occupation;
import br.com.senior.burger_place.domain.occupation.PaymentForm;
import br.com.senior.burger_place.domain.review.Review;
import br.com.senior.burger_place.domain.review.ReviewService;
import br.com.senior.burger_place.domain.review.dto.ReviewRegisterDTO;
import br.com.senior.burger_place.domain.review.dto.ReviewUpdateDTO;
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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(controllers = {ReviewController.class})
@ExtendWith(MockitoExtension.class)
class ReviewControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ReviewController reviewController;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private ReviewService reviewService;

    @Test
    public void registerReview_whenRegistrationReviewForOccupationThatDoesNotExist_shouldReturnHttpStatus404() throws Exception {
        ReviewRegisterDTO dto = new ReviewRegisterDTO(4, "comentário");

        when(reviewService.addReview(1l, dto)).thenThrow(new EntityNotFoundException("Não existe uma ocupação com esse ID"));

        ResultActions response = this.mockMvc
                .perform(MockMvcRequestBuilders.post("/reviews/{occupationId}", 1l)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.objectMapper.writeValueAsString(dto))
                );

        response
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andDo(MockMvcResultHandlers.print());
        verify(reviewService, times(1)).addReview(1l, dto);
    }

    @Test
    public void registerReview_whenRegistrationDataNotIsValid_shouldReturnHttpStatus400() throws Exception {
        ReviewRegisterDTO dto = new ReviewRegisterDTO(null, null);

        ResultActions response = this.mockMvc
                .perform(MockMvcRequestBuilders.post("/reviews/{occupationId}", 1l)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.objectMapper.writeValueAsString(dto))
                );

        response
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(jsonPath(
                        "$[0].field",
                        CoreMatchers.is("grade"))
                )
                .andExpect(jsonPath(
                        "$[0].message",
                        CoreMatchers.is("must not be null")
                ))
                .andDo(MockMvcResultHandlers.print());
        verify(reviewService, never()).addReview(any(), any());
    }

    @Test
    public void registerReview_whenRegistrationDataIsValid_shouldReturnHttpStatus201WithReviewData() throws Exception {
        ReviewRegisterDTO dto = new ReviewRegisterDTO(4, "comentario");
        Review review = new Review(1l, dto);
        review.setId(10l);

        when(reviewService.addReview(1l, dto)).thenReturn(review);

        ResultActions response = this.mockMvc
                .perform(MockMvcRequestBuilders.post("/reviews/{occupationId}", 1l)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.objectMapper.writeValueAsString(dto))
                );

        String expectedLocation = String.format("http://localhost/reviews/%d", 10l);

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
                        CoreMatchers.is(review.getId().intValue())
                ))
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$.grade",
                        CoreMatchers.is(review.getGrade())
                ))
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$.comment",
                        CoreMatchers.containsString(review.getComment())
                ))
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$.occupation.id",
                        CoreMatchers.is(1)
                ));
    }

    @Test
    public void listAllReview_whenExistReviews_shouldReturnStatus200WithReviews() throws Exception {
        Review review1 = new Review(1l, 4, "comentario", mock(Occupation.class));
        Review review2 = new Review(2l, 5, "comentario", mock(Occupation.class));

        PageImpl<Review> somePage = new PageImpl<>(
                Arrays.asList(
                        review1,
                        review2
                ),
                Pageable.ofSize(10), 10);
        when(this.reviewService.listAllReview(any(Pageable.class))).thenReturn(somePage);

        ResultActions response = this.mockMvc
                .perform(MockMvcRequestBuilders.get("/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                );
        response
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.content.size()", CoreMatchers.is(2)))
                .andDo(MockMvcResultHandlers.print());
        verify(reviewService, times(1)).listAllReview(any(Pageable.class));
    }

    @Test
    public void listAllReview_whenReviewsDoNotExist_shouldReturnStatus200WithOutAnyReviews() throws Exception {

        PageImpl<Review> somePage = new PageImpl<>(
                List.of(),
                Pageable.ofSize(10), 10);

        when(this.reviewService.listAllReview(any(Pageable.class))).thenReturn(somePage);

        ResultActions response = this.mockMvc
                .perform(MockMvcRequestBuilders.get("/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                );
        response
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.content.size()", CoreMatchers.is(0)))
                .andDo(MockMvcResultHandlers.print());
        verify(reviewService, times(1)).listAllReview(any(Pageable.class));
    }

    @Test
    public void listReviewById_whenExistReview_shouldReturnStatus200WithReview() throws Exception {
        Occupation occupation = new Occupation(5L, LocalDateTime.now(), null, 2, PaymentForm.CARTAO_CREDITO, new ArrayList<>(), new Board(), new HashSet<>(), true);
        Review someReview = new Review(1l, 4, "comentario", occupation);
        when(reviewService.listReviewById(1l)).thenReturn(someReview);

        ResultActions response = this.mockMvc
                .perform(MockMvcRequestBuilders.get("/reviews/{id}", 1l)
                        .contentType(MediaType.APPLICATION_JSON)
                );
        response
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(result -> {
                    Review responseResult = this.objectMapper.readValue(
                            result.getResponse().getContentAsString(),
                            Review.class
                    );
                    Assertions.assertEquals(someReview.getGrade(), responseResult.getGrade());
                    Assertions.assertEquals(someReview.getComment(), responseResult.getComment());
                    Assertions.assertEquals(someReview.getId(), responseResult.getId());
                    Assertions.assertEquals(someReview.getOccupation().getId(), responseResult.getOccupation().getId());
                });
    }

    @Test
    public void listReviewById_whenReviewDoesNotExist_shouldReturnStatus404() throws Exception {
        when(this.reviewService.listReviewById(anyLong()))
                .thenThrow(new EntityNotFoundException("Avaliação não existe"));

        ResultActions response = this.mockMvc
                .perform(MockMvcRequestBuilders.get("/reviews/{id}", 1l)
                        .contentType(MediaType.APPLICATION_JSON)
                );
        response
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(content().string(org.hamcrest.Matchers.not(org.hamcrest.Matchers.isEmptyOrNullString())))
                .andDo(MockMvcResultHandlers.print());
        verify(reviewService, times(1)).listReviewById(anyLong());
    }

    @Test
    public void updateReview_whenDtoIsValid_shouldReturnStatus200() throws Exception {
        Occupation occupation = new Occupation(5L, LocalDateTime.now(), null, 2, PaymentForm.CARTAO_CREDITO, new ArrayList<>(), new Board(), new HashSet<>(), true);
        Review review = new Review(1l, 5, "comentario", occupation);

        ReviewUpdateDTO updatedDto = new ReviewUpdateDTO(4, "novo comentario");
        when(reviewService.updateReview(1l, updatedDto)).thenReturn(new ReviewRegisterDTO(4, "novo comentario"));

        ResultActions response = this.mockMvc
                .perform(MockMvcRequestBuilders.put("/reviews/{id}", 1l)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.objectMapper.writeValueAsString(updatedDto))
                );
        response
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$.grade",
                        CoreMatchers.is(updatedDto.grade())
                ))
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$.comment",
                        CoreMatchers.is(updatedDto.comment())
                ))
                .andDo(MockMvcResultHandlers.print());

        verify(reviewService, times(1)).updateReview(1l, updatedDto);
    }

    @Test
    public void deleteReview_whenReviewExists_sholdReturnStatus2004() throws Exception {
        Review review = new Review(1l, 3, "comentario", mock(Occupation.class));

        ResultActions response = this.mockMvc
                .perform(MockMvcRequestBuilders.delete("/reviews/{id}", 1l)
                        .contentType(MediaType.APPLICATION_JSON));

        response
                .andExpect(MockMvcResultMatchers.status().isNoContent())
                .andDo(MockMvcResultHandlers.print());
        verify(reviewService, times(1)).deleteReview(review.getId());
    }
}