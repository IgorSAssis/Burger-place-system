package br.com.senior.burger_place.domain.review;

import br.com.senior.burger_place.domain.occupation.Occupation;
import br.com.senior.burger_place.domain.review.dto.ReviewRegisterDTO;
import br.com.senior.burger_place.domain.review.dto.ReviewUpdateDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;


@ExtendWith(MockitoExtension.class)
class ReviewTest {

    @Test
    public void Review_whenCommentIsEmpty_shouldNotUpdateTheComment(){

        Review review = new Review(1L, "");
        assertNull(review.getComment());
        assertNull(review.getId());
        assertEquals(1L, review.getOccupation().getId());
    }
    @Test
    public void Review_whenTheCommentHasOnlyEmpty_shouldNotUpdateTheComment(){

        Review review = new Review(1L, "               ");
        assertNull(review.getComment());
    }
    @Test
    public void Review_whenTheCommentDataIsValid_shouldUpdateTheComment(){

        Review review = new Review(1L, "Comentário");
        assertEquals("Comentário", review.getComment());
    }

    @Test
    public void updateInformation_whenTheCommentHasOnlyEmpty_shouldNotUpdateTheComment(){
        Review review = new Review(1L, "Comentário");
        ReviewUpdateDTO dto = new ReviewUpdateDTO("           ");

        review.updateInformation(dto);

        assertEquals("Comentário", review.getComment());
    }
    @Test
    public void updateInformation_whenCommentIsEmpty_shouldNotUpdateTheComment(){
        Review review = new Review();
        ReviewUpdateDTO dto = new ReviewUpdateDTO("");

        review.updateInformation(dto);

        assertNull(review.getComment());
    }

    @Test
    public void updateInformation_whenTheCommentDataIsValid_shouldUpdateTheComment(){

        Review review = new Review(1L, "Comentário antigo");
        ReviewUpdateDTO dto = new ReviewUpdateDTO("Novo comentário");

        review.updateInformation(dto);
        assertEquals("Novo comentário", review.getComment());
    }
}