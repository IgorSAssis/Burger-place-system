package br.com.senior.burger_place.domain.review;

import br.com.senior.burger_place.domain.review.dto.ReviewUpdateDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;


@ExtendWith(MockitoExtension.class)
class ReviewTest {
//
//    @Test
//    public void Review_whenCommentIsEmpty_shouldNotUpdateTheComment(){
//
//        Review review = new Review(1L, "");
//        assertNull(review.getComment());
//        assertNull(review.getId());
//        assertEquals(1L, review.getOccupation().getId());
//    }
//    @Test
//    public void Review_whenTheCommentHasOnlyEmpty_shouldNotUpdateTheComment(){
//
//        Review review = new Review(1L, "               ");
//        assertNull(review.getComment());
//    }
//    @Test
//    public void Review_whenTheCommentDataIsValid_shouldUpdateTheComment(){
//
//        Review review = new Review(1L, "Comentário");
//        assertEquals("Comentário", review.getComment());
//    }
//
//    @Test
//    public void updateInformation_whenTheCommentHasOnlyEmpty_shouldNotUpdateTheComment(){
//        Review review = new Review(1L, "Comentário");
//        ReviewUpdateDTO dto = new ReviewUpdateDTO("           ");
//
//        review.updateInformation(dto);
//
//        assertEquals("Comentário", review.getComment());
//    }
//    @Test
//    public void updateInformation_whenCommentIsEmpty_shouldNotUpdateTheComment(){
//        Review review = new Review();
//        ReviewUpdateDTO dto = new ReviewUpdateDTO("");
//
//        review.updateInformation(dto);
//
//        assertNull(review.getComment());
//    }
//
//    @Test
//    public void updateInformation_whenTheCommentDataIsValid_shouldUpdateTheComment(){
//
//        Review review = new Review(1L, "Comentário antigo");
//        ReviewUpdateDTO dto = new ReviewUpdateDTO("Novo comentário");
//
//        review.updateInformation(dto);
//        assertEquals("Novo comentário", review.getComment());
//    }
}