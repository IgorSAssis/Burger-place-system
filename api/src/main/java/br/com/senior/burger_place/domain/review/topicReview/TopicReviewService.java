package br.com.senior.burger_place.domain.review.topicReview;

import br.com.senior.burger_place.domain.review.ReviewRepository;
import br.com.senior.burger_place.domain.review.topicReview.dto.CreateTopicReviewDTO;
import br.com.senior.burger_place.domain.review.topicReview.dto.ListingTopicReviewDTO;
import br.com.senior.burger_place.domain.review.topicReview.dto.TopicReviewRegisterDTO;
import br.com.senior.burger_place.domain.review.topicReview.dto.TopicReviewUpdateDTO;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TopicReviewService {

    @Autowired
    TopicReviewRepository repository;
    @Autowired
    private ReviewRepository reviewRepository;

    public ListingTopicReviewDTO register(CreateTopicReviewDTO dto) {
        if (!this.reviewRepository.existsById(dto.reviewId())) {
            throw new IllegalArgumentException("Avaliação não existe");
        }

        TopicReview topicReview = repository.save(new TopicReview(dto.grade(), dto.category(), dto.reviewId()));

        return new ListingTopicReviewDTO(topicReview);
    }

    public void deleteTopicReview(Long id) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("Avaliação não existe");
        }
        repository.deleteById(id);
    }

    public TopicReviewRegisterDTO updateTopicReview(Long id, TopicReviewUpdateDTO dto) {
        Optional<TopicReview> optionalTopicReview = repository.findById(id);
        if (optionalTopicReview.isEmpty()) {
            throw new EntityNotFoundException("Avaliação não existe");
        }
        TopicReview topicReview = optionalTopicReview.get();
        topicReview.updateInformation(dto);
        TopicReviewRegisterDTO responseData = new TopicReviewRegisterDTO(
                topicReview.getGrade(),
                topicReview.getCategory());

        return responseData;
    }
    public Page<TopicReview> listTopicReviewByCategory(Category category, Pageable pageable) {
        Page<TopicReview> topicReviews = repository.findByCategory(category, pageable);
        return topicReviews;
    }
}
