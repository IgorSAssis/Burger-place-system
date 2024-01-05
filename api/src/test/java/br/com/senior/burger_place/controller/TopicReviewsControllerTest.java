package br.com.senior.burger_place.controller;

import br.com.senior.burger_place.domain.review.topicReview.Category;
import br.com.senior.burger_place.domain.review.topicReview.TopicReview;
import br.com.senior.burger_place.domain.review.topicReview.TopicReviewService;
import br.com.senior.burger_place.domain.review.topicReview.dto.TopicReviewRegisterDTO;
import br.com.senior.burger_place.domain.review.topicReview.dto.TopicReviewUpdateDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import org.hamcrest.CoreMatchers;
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

import static br.com.senior.burger_place.domain.review.topicReview.Category.AMBIENTE;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(controllers = {TopicReviewsController.class})
@ExtendWith(MockitoExtension.class)
class TopicReviewsControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private TopicReviewsController topicReviewsController;
    @MockBean
    private TopicReviewService topicReviewService;
    @Autowired
    private ObjectMapper objectMapper;

    TopicReviewsControllerTest() throws Exception {
    }

    @Test
    public void listAllTByCategory_whenExistReview_shouldReturnStatus200WithAllReviewWithCategoryAMBIENTE() throws Exception {
        TopicReview topicReview1 = new TopicReview(5, AMBIENTE, 1l);
        TopicReview topicReview2 = new TopicReview(3, AMBIENTE, 2l);

        PageImpl<TopicReview> somePage = new PageImpl<>(
                Arrays.asList(
                        topicReview1,
                        topicReview2
                ),
                Pageable.ofSize(10), 10);

        when(topicReviewService.listTopicReviewByCategory(eq(AMBIENTE), any(Pageable.class))).thenReturn(somePage);

        ResultActions response = this.mockMvc
                .perform(MockMvcRequestBuilders.get("/reviews/topics")
                        .param("category", "ambiente")
                        .contentType(MediaType.APPLICATION_JSON)
                );
        response
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].grade").value(5))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].category").value(AMBIENTE.name()))
                .andExpect(jsonPath("$.content.size()", CoreMatchers.is(2)))
                .andDo(MockMvcResultHandlers.print());

        verify(topicReviewService, times(1)).listTopicReviewByCategory(eq(AMBIENTE), any(Pageable.class));
    }

    @Test
    public void listAllTByCategory_whenReviewDoesNotExist_shouldReturnStatus404() throws Exception {
        when(topicReviewService.listTopicReviewByCategory(any(), any(Pageable.class))).thenThrow(IllegalArgumentException.class);

        ResultActions response = this.mockMvc
                .perform(MockMvcRequestBuilders.get("/reviews/topics")
                        .param("category", "ambiente")
                        .contentType(MediaType.APPLICATION_JSON)
                );

        response
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(content().string(org.hamcrest.Matchers.not(org.hamcrest.Matchers.isEmptyOrNullString())))
                .andDo(MockMvcResultHandlers.print());
        verify(topicReviewService, times(1)).listTopicReviewByCategory(any(), any(Pageable.class));

    }



    @Test
    public void update__whenDtoIsValid_shouldReturnStatus200() throws Exception {
        TopicReview topicReview = new TopicReview(5, AMBIENTE, 1l);
        TopicReviewRegisterDTO topicReviewDTO = new TopicReviewRegisterDTO(5, AMBIENTE);
        TopicReviewUpdateDTO topicUpdate = new TopicReviewUpdateDTO(2);

        when(topicReviewService.updateTopicReview(topicReview.getReviewId(), topicUpdate)).thenReturn(topicReviewDTO);

        ResultActions response = this.mockMvc
                .perform(MockMvcRequestBuilders.put("/reviews/topics/{id}", topicReview.getReviewId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.objectMapper.writeValueAsString(topicUpdate))
                );

        response
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.grade").value(5))
                .andExpect(MockMvcResultMatchers.jsonPath("$.category").value(AMBIENTE.name()))
                .andDo(MockMvcResultHandlers.print());
        verify(topicReviewService, times(1)).updateTopicReview(topicReview.getReviewId(), topicUpdate);
    }

    @Test
    public void delete_whenTopicReviewExist_shouldReturnStatus2004() throws Exception {
        TopicReview topicReview = new TopicReview(5, AMBIENTE, 1l);
        topicReview.setId(2l);

        ResultActions response = this.mockMvc
                .perform(MockMvcRequestBuilders.delete("/reviews/topics/{id}", 2l)
                        .contentType(MediaType.APPLICATION_JSON)
                );
        response
                .andExpect(MockMvcResultMatchers.status().isNoContent())
                .andDo(MockMvcResultHandlers.print());
        verify(topicReviewService, times(1)).deleteTopicReview(2l);
    }
}