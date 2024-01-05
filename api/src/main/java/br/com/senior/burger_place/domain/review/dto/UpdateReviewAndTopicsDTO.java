package br.com.senior.burger_place.domain.review.dto;

import br.com.senior.burger_place.domain.review.topicReview.dto.UpdateTopicReviewDTO;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.Optional;

public record UpdateReviewAndTopicsDTO(
        @NotNull
        Long occupationId,
        String comment,
        List<UpdateTopicReviewDTO> items
) {
}
