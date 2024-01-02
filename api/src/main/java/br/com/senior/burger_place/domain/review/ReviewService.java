package br.com.senior.burger_place.domain.review;

import br.com.senior.burger_place.domain.review.dto.ReviewRegisterDTO;
import br.com.senior.burger_place.domain.review.dto.ReviewUpdateDTO;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ReviewService {
    @Autowired
    ReviewRepository repository;
    public Review addReview(Long occupationId, ReviewRegisterDTO data) {
        if (!repository.verifyOccupationExists(occupationId)){
                throw new EntityNotFoundException("Não existe uma ocupação com esse ID");
        }
        return repository.save(new Review(occupationId, data));
    }

    public ReviewRegisterDTO updateReview(Long id, ReviewUpdateDTO data) {
        Optional<Review> optionalReview = repository.findById(id);
        if (optionalReview.isEmpty()){
            throw new EntityNotFoundException("Avaliação não existe");
        }
        Review review = optionalReview.get();
        review.updateInformation(data);
        ReviewRegisterDTO responseData = new ReviewRegisterDTO(review.getGrade(), review.getComment());
        return responseData;
    }

    public void deleteReview(Long id) {
        if (!repository.existsById(id)){
            throw new EntityNotFoundException("Avaliação não existe");
        }
        repository.deleteById(id);
    }

    public Page<Review> listAllReview(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Review listReviewById(Long id) {
        Optional<Review> optionalReview = repository.findById(id);
        if (optionalReview.isEmpty()){
            throw new EntityNotFoundException("Avaliação não existe");
        }
        Review review = optionalReview.get();
        return review;
    }
}
