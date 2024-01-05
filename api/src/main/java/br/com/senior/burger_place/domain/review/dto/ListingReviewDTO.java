package br.com.senior.burger_place.domain.review.dto;

import br.com.senior.burger_place.domain.review.Review;
import br.com.senior.burger_place.domain.review.topicReview.TopicReview;
import br.com.senior.burger_place.domain.review.topicReview.dto.ListingTopicReviewDTO;

import java.util.List;

public record ListingReviewDTO(
        Long id,
        String comment,
        List<ListingTopicReviewDTO> topicReviews

) {
    public ListingReviewDTO(Review data) {
        this(data.getId(), data.getComment(), data.getTopicReviews().stream().map(ListingTopicReviewDTO::new).toList());

    }

}
