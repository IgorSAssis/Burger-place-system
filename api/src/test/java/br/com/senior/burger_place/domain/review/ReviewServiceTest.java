package br.com.senior.burger_place.domain.review;

import br.com.senior.burger_place.domain.review.dto.ReviewRegisterDTO;
import br.com.senior.burger_place.domain.review.dto.ReviewUpdateDTO;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @InjectMocks
    ReviewService service;
    @Mock
    private ReviewRepository repository;
    @Captor
    ArgumentCaptor<Review> reviewCaptor;

    @Captor
    ArgumentCaptor<Pageable> pageableCaptor;


    @Test
    public void addReview_whenOccupationNotExistis_soludThrowException(){
        Long occupationId = 1l;
        ReviewRegisterDTO dto = mock(ReviewRegisterDTO.class);
        when(repository.verifyOccupationExists(occupationId)).thenReturn(false);

        EntityNotFoundException e = assertThrows(EntityNotFoundException.class, ()-> service.addReview(occupationId, dto));
        assertEquals("Não existe uma ocupação com esse ID", e.getMessage());
    }
    @Test
    public void addReview_whenOccupationExistis_soludSaveReview(){
        Long occupationId = 1l;
        ReviewRegisterDTO dto = new ReviewRegisterDTO(5, "Comentário");
        Review review = new Review(occupationId, dto);

        when(repository.verifyOccupationExists(occupationId)).thenReturn(true);
        service.addReview(occupationId, dto);

        verify(repository, times(1)).verifyOccupationExists(occupationId);
        verify(repository, times(1)).save(reviewCaptor.capture());
        Review reviewCaptured = reviewCaptor.getValue();

        assertEquals(review.getComment(), reviewCaptured.getComment());
        assertEquals(review.getGrade(), reviewCaptured.getGrade());
        assertEquals(review.getOccupation().getId(), reviewCaptured.getOccupation().getId());
    }

    @Test
    public void updateReview_whenOptionalReviewIsEmpty_shouldThrowException(){
        Long reviewId = 1l;
        ReviewUpdateDTO dto = mock(ReviewUpdateDTO.class);

        when(repository.findById(reviewId)).thenReturn(Optional.empty());

        EntityNotFoundException e = assertThrows(EntityNotFoundException.class,
                ()-> service.updateReview(reviewId, dto));

        assertEquals("Avaliação não existe", e.getMessage());
    }

    @Test
    public void updateReview_whenOptionalReviewIsValid_shouldCallupdateInformationMethod(){
        Long reviewId = 1l;
        ReviewUpdateDTO dto = new ReviewUpdateDTO(10, "Comentário");
        Review existingReview = mock(Review.class);

        when(repository.findById(reviewId)).thenReturn(Optional.of(existingReview));
        service.updateReview(reviewId, dto);

        verify(existingReview, times(1)).updateInformation(dto);
    }
    @Test
    public void updateReview_whenOptionalReviewIsValid_shouldReturnResponseData(){
        ReviewUpdateDTO newdto = new ReviewUpdateDTO(5, "Comentário");
        ReviewRegisterDTO oldDto = new ReviewRegisterDTO(3, "Comentário");
        Long reviewId = 1l;
        Review existingReview = new Review(2l, oldDto);

        when(repository.findById(reviewId)).thenReturn(Optional.of(existingReview));

        ReviewRegisterDTO response = service.updateReview(reviewId, newdto);

        assertEquals(response.grade(), newdto.grade());
        assertEquals(response.comment(), newdto.comment());
    }

    @Test
    public void deleteReview_whenReviewIdNotExists_shouldThrowException(){

        Long id = 1l;
        when(repository.existsById(id)).thenReturn(false);

        EntityNotFoundException e = assertThrows(EntityNotFoundException.class, ()-> service.deleteReview(id));
        assertEquals("Avaliação não existe", e.getMessage());

        verify(repository, never()).deleteById(any(Long.class));
    }
    @Test
    public void deleteReview_whenReviewIdExists_shouldCalldeleteByIdMethod(){

        Long id = 1l;
        when(repository.existsById(id)).thenReturn(true);

        service.deleteReview(id);

        verify(repository, times(1)).deleteById(id);
    }

    @Test
    public void listAllReview_shouldReturnAllReviewAvailable(){
        Pageable pageable = Pageable.unpaged();

        service.listAllReview(pageable);

        verify(repository, times(1)).findAll(pageableCaptor.capture());
        assertEquals(pageable, pageableCaptor.getValue());
        assertDoesNotThrow(()-> service.listAllReview(pageable));
    }

    @Test public void listReviewById_whenOptionalRviewIsEmpty_ShouldThrowException(){
        Long id = 1l;

        when(repository.findById(id)).thenReturn(Optional.empty());

        Exception e = assertThrows(EntityNotFoundException.class, ()-> service.listReviewById(id));
        assertEquals("Avaliação não existe", e.getMessage());
        verify(repository, times(1)).findById(id);
    }
    @Test public void listReviewById_whenOptionalRviewIdValid_ShouldReturnReview(){
        Long id = 1l;
        Review expectedReview = new Review(id, new ReviewRegisterDTO(5, "Comentário"));

        when(repository.findById(id)).thenReturn(Optional.of(expectedReview));

        Review response = service.listReviewById(id);

        assertEquals(expectedReview, response);


        verify(repository, times(1)).findById(id);
    }


}