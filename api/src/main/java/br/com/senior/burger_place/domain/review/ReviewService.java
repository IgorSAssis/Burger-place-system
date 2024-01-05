package br.com.senior.burger_place.domain.review;

import br.com.senior.burger_place.domain.review.dto.ListingReviewDTO;
import br.com.senior.burger_place.domain.review.dto.ReviewRegisterDTO;
import br.com.senior.burger_place.domain.review.dto.ReviewUpdateDTO;
import br.com.senior.burger_place.domain.review.topicReview.TopicReview;
import br.com.senior.burger_place.domain.review.topicReview.TopicReviewRepository;
import br.com.senior.burger_place.domain.review.topicReview.dto.ListingTopicReviewDTO;
import br.com.senior.burger_place.domain.review.topicReview.dto.TopicReviewRegisterDTO;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ReviewService {
    @Autowired
    ReviewRepository repository;
    @Autowired
    private TopicReviewRepository topicReviewRepository;

    public ListingReviewDTO addReview(ReviewRegisterDTO data) {
        if (!repository.verifyOccupationExists(data.occupationId())) {
            throw new EntityNotFoundException("Não existe uma ocupação com esse ID");
        }

        if (data.items().isEmpty()) {
            throw new IllegalArgumentException("Não pode ser nulo");
        }
        for (TopicReviewRegisterDTO item : data.items()) {
            if (item.grade() == null) {
                throw new IllegalArgumentException("Grade não pode ser nulo");
            }
            if (item.category() == null) {
                throw new IllegalArgumentException("Category não pode ser nulo");
            }
        }

        Review review = repository.save(new Review(data.occupationId(), data.comment()));
        List<TopicReview> list = data.items().stream().map(item -> new TopicReview(item.grade(), item.category(), review.getId())).toList();
        topicReviewRepository.saveAll(list);
        review.setTopicReviews(list);

        return new ListingReviewDTO(review);
    }

    public ReviewRegisterDTO updateReview(Long id, ReviewUpdateDTO data) {
        Optional<Review> optionalReview = repository.findById(id);
        if (optionalReview.isEmpty()) {
            throw new EntityNotFoundException("Avaliação não existe");
        }
        Review review = optionalReview.get();
        review.updateInformation(data);
        ReviewRegisterDTO responseData = new ReviewRegisterDTO(review.getOccupation().getId(), review.getComment(),List.of());
        return responseData;
    }

    public void deleteReview(Long id) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("Avaliação não existe");
        }
        repository.deleteById(id);
    }

    public Page<Review> listAllReview(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public ListingReviewDTO listReviewById(Long id) {
        Optional<Review> optionalReview = repository.findById(id);
        if (optionalReview.isEmpty()) {
            throw new EntityNotFoundException("Avaliação não existe");
        }
        Review review = optionalReview.get();
        return new ListingReviewDTO(review);
    }
}
