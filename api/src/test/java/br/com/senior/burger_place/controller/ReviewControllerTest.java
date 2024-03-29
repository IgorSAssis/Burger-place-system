package br.com.senior.burger_place.controller;

import br.com.senior.burger_place.domain.review.Review;
import br.com.senior.burger_place.domain.review.ReviewService;
import br.com.senior.burger_place.domain.review.dto.ListingReviewDTO;
import br.com.senior.burger_place.domain.review.dto.ReviewRegisterDTO;
import br.com.senior.burger_place.domain.review.dto.ReviewUpdateDTO;
import br.com.senior.burger_place.domain.review.topicReview.TopicReview;
import br.com.senior.burger_place.domain.review.topicReview.dto.TopicReviewRegisterDTO;
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
import java.util.List;

import static br.com.senior.burger_place.domain.review.topicReview.Category.AMBIENTE;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(controllers = {ReviewController.class})
@ExtendWith(MockitoExtension.class)
class ReviewControllerTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//    @Autowired
//    private ReviewController reviewController;
//    @Autowired
//    private ObjectMapper objectMapper;
//    @MockBean
//    private ReviewService reviewService;
//
//    @Test
//    public void registerReview_whenRegistrationReviewForOccupationThatDoesNotExist_shouldReturnHttpStatus404() throws Exception {
//        ReviewRegisterDTO dto = new ReviewRegisterDTO(1L, "Comentário",
//                List.of(new TopicReviewRegisterDTO(5, AMBIENTE)));
//
//        when(reviewService.addReview(dto)).thenThrow(new EntityNotFoundException("Não existe uma ocupação com esse ID"));
//
//        ResultActions response = this.mockMvc
//                .perform(MockMvcRequestBuilders.post("/reviews")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(this.objectMapper.writeValueAsString(dto))
//                );
//
//        response
//                .andExpect(MockMvcResultMatchers.status().isNotFound())
//                .andDo(MockMvcResultHandlers.print());
//        verify(reviewService, times(1)).addReview(dto);
//    }
//
//    @Test
//    public void registerReview_whenItemDataNotEmpty_shouldReturnHttpStatus400() throws Exception {
//        ReviewRegisterDTO dto = new ReviewRegisterDTO(1l, "comentário", null);
//
//        ResultActions response = this.mockMvc
//                .perform(MockMvcRequestBuilders.post("/reviews")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(this.objectMapper.writeValueAsString(dto))
//                );
//
//        response
//                .andExpect(MockMvcResultMatchers.status().isBadRequest())
//                .andExpect(jsonPath(
//                        "$[0].field",
//                        CoreMatchers.is("items"))
//                )
//                .andExpect(jsonPath(
//                        "$[0].message",
//                        CoreMatchers.is("must not be empty")
//                ))
//                .andDo(MockMvcResultHandlers.print());
//        verify(reviewService, never()).addReview(any());
//    }
//
//    @Test
//    public void registerReview_whenRegistrationDataIsValid_shouldReturnHttpStatus201WithReviewData() throws Exception {
//        ReviewRegisterDTO dto = new ReviewRegisterDTO(1L, "Comentário",
//                List.of(new TopicReviewRegisterDTO(5, AMBIENTE)));
//        Review review = new Review(dto.occupationId(), dto.comment());
//        review.setId(10l);
//        review.setTopicReviews(dto.items().stream().map(item -> new TopicReview(item.grade(), item.category(), review.getId())).toList());
//        ListingReviewDTO listingReviewDTO = new ListingReviewDTO(review);
//
//        when(reviewService.addReview(dto)).thenReturn(listingReviewDTO);
//
//        ResultActions response = this.mockMvc
//                .perform(MockMvcRequestBuilders.post("/reviews")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(this.objectMapper.writeValueAsString(dto))
//                );
//
//        String expectedLocation = String.format("http://localhost/reviews/%d", 10l);
//
//        response
//                .andExpect(MockMvcResultMatchers.status().isCreated())
//                .andExpect(result -> {
//                    Assertions.assertTrue(result.getResponse().containsHeader("Location"));
//                })
//                .andExpect(result -> {
//                    Assertions.assertEquals(
//                            expectedLocation,
//                            result.getResponse().getHeader("Location")
//                    );
//                })
//                .andExpect(result -> {
//                    Assertions.assertEquals(
//                            expectedLocation,
//                            result.getResponse().getRedirectedUrl()
//                    );
//                })
//                .andExpect(MockMvcResultMatchers.jsonPath(
//                        "$.id",
//                        CoreMatchers.is(review.getId().intValue())
//                ))
//                .andExpect(MockMvcResultMatchers.jsonPath(
//                        "$.comment",
//                        CoreMatchers.is(review.getComment())
//                ))
//                .andExpect(MockMvcResultMatchers.jsonPath(
//                        "$.topicReviews[0].grade",
//                        CoreMatchers.is(dto.items().get(0).grade())
//                ))
//                .andExpect(MockMvcResultMatchers.jsonPath(
//                        "$.topicReviews[0].category",
//                        CoreMatchers.equalTo(dto.items().get(0).category().name())
//                ));
//    }
//
//    @Test
//    public void listAllReview_whenExistReviews_shouldReturnStatus200WithReviews() throws Exception {
//        ReviewRegisterDTO dto = new ReviewRegisterDTO(1L, "Comentário",
//                List.of(new TopicReviewRegisterDTO(5, AMBIENTE)));
//        Review review = new Review(dto.occupationId(), dto.comment());
//        review.setTopicReviews(dto.items().stream().map(item -> new TopicReview(item.grade(), item.category(), review.getId())).toList());
//
//
//        PageImpl<Review> somePage = new PageImpl<>(
//                Arrays.asList(review), Pageable.ofSize(10), 10);
//        when(this.reviewService.listAllReview(any(Pageable.class))).thenReturn(somePage);
//
//        ResultActions response = this.mockMvc
//                .perform(MockMvcRequestBuilders.get("/reviews")
//                        .contentType(MediaType.APPLICATION_JSON)
//                );
//        response
//                .andExpect(MockMvcResultMatchers.status().isOk())
//                .andExpect(jsonPath("$.content.size()", CoreMatchers.is(1)))
//                .andDo(MockMvcResultHandlers.print());
//        verify(reviewService, times(1)).listAllReview(any(Pageable.class));
//    }
//
//    @Test
//    public void listAllReview_whenReviewsDoNotExist_shouldReturnStatus200WithOutAnyReviews() throws Exception {
//
//        PageImpl<Review> somePage = new PageImpl<>(
//                List.of(),
//                Pageable.ofSize(10), 10);
//
//        when(this.reviewService.listAllReview(any(Pageable.class))).thenReturn(somePage);
//
//        ResultActions response = this.mockMvc
//                .perform(MockMvcRequestBuilders.get("/reviews")
//                        .contentType(MediaType.APPLICATION_JSON)
//                );
//        response
//                .andExpect(MockMvcResultMatchers.status().isOk())
//                .andExpect(jsonPath("$.content.size()", CoreMatchers.is(0)))
//                .andDo(MockMvcResultHandlers.print());
//        verify(reviewService, times(1)).listAllReview(any(Pageable.class));
//    }
//
//    @Test
//    public void listReviewById_whenExistReview_shouldReturnStatus200WithReview() throws Exception {
//        ReviewRegisterDTO dto = new ReviewRegisterDTO(1L, "Comentario",
//                List.of(new TopicReviewRegisterDTO(5, AMBIENTE)));
//        Review review = new Review(dto.occupationId(), dto.comment());
//        review.setId(1l);
//        review.setTopicReviews(dto.items().stream().map(item -> new TopicReview(item.grade(), item.category(), review.getId())).toList());
//        ListingReviewDTO listingReviewDTO = new ListingReviewDTO(review);
//
//        when(reviewService.listReviewById(1l)).thenReturn(listingReviewDTO);
//
//        ResultActions response = this.mockMvc
//                .perform(MockMvcRequestBuilders.get("/reviews/{id}", 1l)
//                        .contentType(MediaType.APPLICATION_JSON)
//                );
//        response
//                .andExpect(MockMvcResultMatchers.status().isOk())
//                .andExpect(result -> {
//                    Review responseResult = this.objectMapper.readValue(
//                            result.getResponse().getContentAsString(),
//                            Review.class
//                    );
//                    Assertions.assertEquals(listingReviewDTO.comment(), responseResult.getComment());
//                    Assertions.assertEquals(listingReviewDTO.topicReviews().get(0).grade(), responseResult.getTopicReviews().get(0).getGrade());
//                    Assertions.assertEquals(listingReviewDTO.topicReviews().get(0).category(), responseResult.getTopicReviews().get(0).getCategory());
//                    Assertions.assertEquals(listingReviewDTO.topicReviews().get(0).id(), responseResult.getTopicReviews().get(0).getId());
//
//                });
//    }
//
//
//    @Test
//    public void listReviewById_whenReviewDoesNotExist_shouldReturnStatus404() throws Exception {
//        when(this.reviewService.listReviewById(anyLong()))
//                .thenThrow(new EntityNotFoundException("Avaliação não existe"));
//
//        ResultActions response = this.mockMvc
//                .perform(MockMvcRequestBuilders.get("/reviews/{id}", 1l)
//                        .contentType(MediaType.APPLICATION_JSON)
//                );
//        response
//                .andExpect(MockMvcResultMatchers.status().isNotFound())
//                .andExpect(content().string(org.hamcrest.Matchers.not(org.hamcrest.Matchers.isEmptyOrNullString())))
//                .andDo(MockMvcResultHandlers.print());
//        verify(reviewService, times(1)).listReviewById(anyLong());
//    }
//
//    @Test
//    public void updateReview_whenDtoIsValid_shouldReturnStatus200() throws Exception {
//        ReviewRegisterDTO dto = new ReviewRegisterDTO(1L, "Comentario", List.of(new TopicReviewRegisterDTO(5, AMBIENTE)));
//        Review review = new Review(dto.occupationId(), dto.comment());
//        review.setId(1l);
//        review.setTopicReviews(dto.items().stream().map(item -> new TopicReview(item.grade(), item.category(), review.getId())).toList());
//
//        ReviewUpdateDTO updatedDto = new ReviewUpdateDTO("novo comentario");
//        when(reviewService.updateReview(1l, updatedDto)).thenReturn(new ReviewRegisterDTO(4l, "novo comentario", null));
//
//        ResultActions response = this.mockMvc
//                .perform(MockMvcRequestBuilders.put("/reviews/{id}", 1l)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(this.objectMapper.writeValueAsString(updatedDto))
//                );
//        response
//                .andExpect(MockMvcResultMatchers.status().isOk())
//                .andExpect(MockMvcResultMatchers.jsonPath(
//                        "$.comment",
//                        CoreMatchers.is(updatedDto.comment())
//                ))
//                .andDo(MockMvcResultHandlers.print());
//
//        verify(reviewService, times(1)).updateReview(1l, updatedDto);
//    }
//
//    @Test
//    public void deleteReview_whenReviewExists_sholdReturnStatus2004() throws Exception {
//        ReviewRegisterDTO dto = new ReviewRegisterDTO(1L, "Comentario", List.of(new TopicReviewRegisterDTO(5, AMBIENTE)));
//        Review review = new Review(dto.occupationId(), dto.comment());
//        review.setId(1l);
//        review.setTopicReviews(dto.items().stream().map(item -> new TopicReview(item.grade(), item.category(), review.getId())).toList());
//
//
//        ResultActions response = this.mockMvc
//                .perform(MockMvcRequestBuilders.delete("/reviews/{id}", 1l)
//                        .contentType(MediaType.APPLICATION_JSON));
//
//        response
//                .andExpect(MockMvcResultMatchers.status().isNoContent())
//                .andDo(MockMvcResultHandlers.print());
//        verify(reviewService, times(1)).deleteReview(review.getId());
//    }
}