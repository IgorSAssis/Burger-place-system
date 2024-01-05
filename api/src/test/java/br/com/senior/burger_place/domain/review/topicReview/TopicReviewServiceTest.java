package br.com.senior.burger_place.domain.review.topicReview;

import br.com.senior.burger_place.domain.review.Review;
import br.com.senior.burger_place.domain.review.dto.ReviewUpdateDTO;
import br.com.senior.burger_place.domain.review.topicReview.dto.ListingTopicReviewDTO;
import br.com.senior.burger_place.domain.review.topicReview.dto.TopicReviewRegisterDTO;
import br.com.senior.burger_place.domain.review.topicReview.dto.TopicReviewUpdateDTO;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static br.com.senior.burger_place.domain.review.topicReview.Category.AMBIENTE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TopicReviewServiceTest {

    @InjectMocks
    private TopicReviewService service;

    @Mock
    TopicReviewRepository repository;

    @Test
    public void deleteTopicReview_whenTopicReviewIdNotExists_shouldThrowException() {
        Long id = 1l;
        when(repository.existsById(id)).thenReturn(false);

        EntityNotFoundException e = assertThrows(EntityNotFoundException.class, () -> service.deleteTopicReview(id));
        assertEquals("Avaliação não existe", e.getMessage());

        verify(repository, never()).deleteById(any(Long.class));
    }

    @Test
    public void deleteTopicReview_whenTopicReviewIdExists_shouldCalldeleteByIdMethod() {
        Long id = 1l;
        when(repository.existsById(id)).thenReturn(true);

        service.deleteTopicReview(id);

        verify(repository, times(1)).deleteById(id);
    }

    @Test
    public void updateTopicReview_whenOptionalTopicReviewIsEmpty_shouldThrowException() {
        Review review = mock(Review.class);
        TopicReviewUpdateDTO dto = mock(TopicReviewUpdateDTO.class);

        when(repository.findById(anyLong())).thenReturn(Optional.empty());

        EntityNotFoundException e = assertThrows(EntityNotFoundException.class, () -> service.updateTopicReview(anyLong(), dto));
        assertEquals("Avaliação não existe", e.getMessage());
        verify(review, never()).updateInformation(any(ReviewUpdateDTO.class));
    }

    @Test
    public void updateTopicReview__whenOptionalTopicReviewIsValid_shouldReturnResponseData() {
        ListingTopicReviewDTO dto = new ListingTopicReviewDTO(1l, 5, AMBIENTE);
        TopicReview topicReview = new TopicReview(dto);

        TopicReviewUpdateDTO updateDto = new TopicReviewUpdateDTO(4);
        when(repository.findById(1l)).thenReturn(Optional.of(topicReview));

        TopicReviewRegisterDTO result = service.updateTopicReview(1l, updateDto);

        assertEquals(updateDto.grade(), result.grade());
        assertEquals(AMBIENTE, result.category());
        verify(repository, times(1)).findById(1l);
    }

    @Test
    void listTopicReviewByCategory_shouldReturnPageOfTopicReviews() {
        Category category = Category.AMBIENTE;
        Pageable pageable = Pageable.unpaged();
        List<TopicReview> topicReviewList = List.of(
                new TopicReview(5, AMBIENTE, 1l),
                new TopicReview(4, AMBIENTE,2L),
                new TopicReview(3, AMBIENTE,3L)
        );
        Page<TopicReview> expectedPage = new PageImpl<>(topicReviewList, pageable, topicReviewList.size());

        when(repository.findByCategory(category, pageable)).thenReturn(expectedPage);

        Page<TopicReview> result = service.listTopicReviewByCategory(category, pageable);

        verify(repository, times(1)).findByCategory(category, pageable);
        assertEquals(expectedPage, result);
    }

}