package br.com.senior.burger_place.domain.review.topicReview.dto;

import br.com.senior.burger_place.domain.review.topicReview.Category;
import br.com.senior.burger_place.domain.review.topicReview.TopicReview;

import java.util.UUID;

public record ListingTopicReviewDTO(
        UUID id,
        int grade,
        Category category

) {

    public ListingTopicReviewDTO(TopicReview topicReview) {
        this(topicReview.getId(), topicReview.getGrade(), topicReview.getCategory());
    }
}
