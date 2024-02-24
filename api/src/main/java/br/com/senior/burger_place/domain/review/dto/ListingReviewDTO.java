package br.com.senior.burger_place.domain.review.dto;

import br.com.senior.burger_place.domain.review.Review;
import br.com.senior.burger_place.domain.review.topicReview.dto.ListingTopicReviewDTO;

import java.util.List;
import java.util.UUID;

public record ListingReviewDTO(
        UUID id,
        String comment,
        List<ListingTopicReviewDTO> topicReviews

) {
    public ListingReviewDTO(Review data) {
        this(data.getId(), data.getComment(), data.getTopicReviews().stream().map(ListingTopicReviewDTO::new).toList());

    }

}
