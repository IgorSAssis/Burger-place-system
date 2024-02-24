package br.com.senior.burger_place.domain.review.topicReview;

import br.com.senior.burger_place.domain.review.topicReview.dto.ListingTopicReviewDTO;
import br.com.senior.burger_place.domain.review.topicReview.dto.TopicReviewRegisterDTO;
import br.com.senior.burger_place.domain.review.topicReview.dto.TopicReviewUpdateDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "topic_reviews")
@Entity(name = "TopicReview")
public class TopicReview {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private Integer grade;
    @Enumerated(EnumType.STRING)
    private Category category;
    @Column(name = "review_id")
    private UUID reviewId;

    public TopicReview(ListingTopicReviewDTO dto) {
        if (dto.grade() < 1 || dto.grade() > 5) {
            throw new NoSuchElementException("A nota deve ser entre 1 e 5");
        }
        if (dto.category() != null) {
            this.category = dto.category();
        }
        this.grade = dto.grade();
    }

    public TopicReview(Integer grade, Category category, UUID reviewId) {
        this.grade = grade;
        this.category = category;
        this.reviewId = reviewId;
    }

    public void updateInformation(TopicReviewUpdateDTO data) {
        if (data.grade() == null) {
            throw new NoSuchElementException("A nota deve ser entre 1 e 5");
        }
        if (data.grade() < 1 || data.grade() > 5) {
            throw new NoSuchElementException("A nota deve ser entre 1 e 5");
        }
        this.grade = data.grade();
    }
}
