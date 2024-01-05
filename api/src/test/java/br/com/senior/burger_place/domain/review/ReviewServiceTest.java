package br.com.senior.burger_place.domain.review;

import br.com.senior.burger_place.domain.review.dto.ListingReviewDTO;
import br.com.senior.burger_place.domain.review.dto.ReviewRegisterDTO;
import br.com.senior.burger_place.domain.review.dto.ReviewUpdateDTO;
import br.com.senior.burger_place.domain.review.topicReview.TopicReview;
import br.com.senior.burger_place.domain.review.topicReview.TopicReviewRepository;
import br.com.senior.burger_place.domain.review.topicReview.dto.TopicReviewRegisterDTO;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static br.com.senior.burger_place.domain.review.topicReview.Category.AMBIENTE;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @InjectMocks
    ReviewService service;
    @Mock
    private ReviewRepository repository;
    @Mock
    private TopicReviewRepository topicRepository;
    @Captor
    ArgumentCaptor<Review> reviewCaptor;
    @Captor
    ArgumentCaptor<List<TopicReview>> topicCaptor;

    @Captor
    ArgumentCaptor<Pageable> pageableCaptor;


    @Test
    public void addReview_whenOccupationNotExistis_soludThrowException() {
        ReviewRegisterDTO dto = mock(ReviewRegisterDTO.class);
        when(repository.verifyOccupationExists(dto.occupationId())).thenReturn(false);

        EntityNotFoundException e = assertThrows(EntityNotFoundException.class, () -> service.addReview(dto));

        assertEquals("Não existe uma ocupação com esse ID", e.getMessage());

        verify(repository, never()).save(any());
        verify(topicRepository, never()).saveAll(anyList());
    }

    @Test
    public void addReview_whenOccupationExistis_soludSaveReviewAndTopcs() {
        ReviewRegisterDTO dto = new ReviewRegisterDTO(1L, "Comentário",
                List.of(new TopicReviewRegisterDTO(5, AMBIENTE)));
        Review review = new Review(dto.occupationId(), dto.comment());

        when(repository.verifyOccupationExists(dto.occupationId())).thenReturn(true);
        when(repository.save(any(Review.class))).thenReturn(review);
        when(topicRepository.saveAll(anyList())).thenReturn(
                List.of(new TopicReview(5, AMBIENTE, 1L)));

        ListingReviewDTO result = service.addReview(dto);

        verify(repository, times(1)).verifyOccupationExists(dto.occupationId());
        verify(repository, times(1)).save(reviewCaptor.capture());
        verify(topicRepository, times(1)).saveAll(topicCaptor.capture());
        Review reviewCaptured = reviewCaptor.getValue();
        List<TopicReview> topicsCaptured = topicCaptor.getValue();

        assertEquals(review.getComment(), reviewCaptured.getComment());
        assertEquals(5, topicsCaptured.get(0).getGrade());
        assertEquals(AMBIENTE, topicsCaptured.get(0).getCategory());
        assertEquals(review.getOccupation().getId(), reviewCaptured.getOccupation().getId());
        assertEquals("Comentário", result.comment());
        assertEquals(dto.items().get(0).grade(), result.topicReviews().get(0).grade());
        assertEquals(dto.items().get(0).category(), result.topicReviews().get(0).category());
    }

    @Test
    public void updateReview_whenOptionalReviewIsEmpty_shouldThrowException() {
        Review review = mock(Review.class);
        ReviewUpdateDTO dto = mock(ReviewUpdateDTO.class);

        when(repository.findById(anyLong())).thenReturn(Optional.empty());

        EntityNotFoundException e = assertThrows(EntityNotFoundException.class,
                () -> service.updateReview(anyLong(), dto));

        assertEquals("Avaliação não existe", e.getMessage());
        verify(review, never()).updateInformation(any(ReviewUpdateDTO.class));
    }

    @Test
    public void updateReview_whenOptionalReviewIsValid_shouldReturnResponseData() {

        Review review = new Review(1l, "Comentário");
        ReviewUpdateDTO updateDTO = new ReviewUpdateDTO("Novo comentário");
        when(repository.findById(1l)).thenReturn(Optional.of(review));

        ReviewRegisterDTO result = service.updateReview(1l, updateDTO);

        assertEquals(updateDTO.comment(), result.comment());
        assertEquals(review.getOccupation().getId(), result.occupationId());
        verify(repository, times(1)).findById(1l);
    }


    @Test
    public void deleteReview_whenReviewIdNotExists_shouldThrowException() {

        Long id = 1l;
        when(repository.existsById(id)).thenReturn(false);

        EntityNotFoundException e = assertThrows(EntityNotFoundException.class, () -> service.deleteReview(id));
        assertEquals("Avaliação não existe", e.getMessage());

        verify(repository, never()).deleteById(any(Long.class));
    }

    @Test
    public void deleteReview_whenReviewIdExists_shouldCalldeleteByIdMethod() {

        Long id = 1l;
        when(repository.existsById(id)).thenReturn(true);

        service.deleteReview(id);

        verify(repository, times(1)).deleteById(id);
    }

    @Test
    public void listAllReview_shouldReturnAllReviewAvailable() {
        Pageable pageable = Pageable.unpaged();

        service.listAllReview(pageable);

        verify(repository, times(1)).findAll(pageableCaptor.capture());
        assertEquals(pageable, pageableCaptor.getValue());
        assertDoesNotThrow(() -> service.listAllReview(pageable));
    }

    @Test
    public void listReviewById_whenOptionalRviewIsEmpty_ShouldThrowException() {
        Long id = 1l;

        when(repository.findById(id)).thenReturn(Optional.empty());

        Exception e = assertThrows(EntityNotFoundException.class, () -> service.listReviewById(id));
        assertEquals("Avaliação não existe", e.getMessage());
        verify(repository, times(1)).findById(id);
    }

    @Test
    public void listReviewById_whenOptionalReviewIdValid_ShouldReturnReview() {
        Long reviewId = 1L;

        ReviewRegisterDTO dto = new ReviewRegisterDTO(1L, "Comentário",
                List.of(new TopicReviewRegisterDTO(5, AMBIENTE)));
        Review review = new Review(dto.occupationId(), dto.comment());
        review.setTopicReviews(dto.items().stream().map(item -> new TopicReview(item.grade(), item.category(), review.getId())).toList());

        when(repository.findById(reviewId)).thenReturn(Optional.of(review));

        ListingReviewDTO response = service.listReviewById(reviewId);

        assertEquals(review.getComment(), response.comment());
        assertEquals(review.getTopicReviews().size(), response.topicReviews().size());

        verify(repository, times(1)).findById(reviewId);
    }


}