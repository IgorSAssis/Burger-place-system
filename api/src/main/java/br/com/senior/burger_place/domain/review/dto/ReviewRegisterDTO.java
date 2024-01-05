package br.com.senior.burger_place.domain.review.dto;

import br.com.senior.burger_place.domain.review.topicReview.dto.TopicReviewRegisterDTO;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record ReviewRegisterDTO(
        @NotNull
        Long occupationId,
        String comment,
        @NotEmpty
        List<TopicReviewRegisterDTO> items
) {
}
