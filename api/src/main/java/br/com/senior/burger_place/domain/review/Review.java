package br.com.senior.burger_place.domain.review;

import br.com.senior.burger_place.domain.occupation.Occupation;
import br.com.senior.burger_place.domain.review.dto.ReviewUpdateDTO;
import br.com.senior.burger_place.domain.review.topicReview.TopicReview;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "reviews")
@Entity(name = "Review")
@JsonIgnoreProperties({"hibernateLazyInitializer"})
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String comment;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "occupation_id")
    @JsonIgnoreProperties({"beginOccupation", "endOccupation", "peopleCount", "paymentForm", "orderItems", "board", "customers", "active"})
    private Occupation occupation;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id")
    private List<TopicReview> topicReviews;

    public Review(Long occupationId, String comment) {
        if (comment != null && !comment.trim().isEmpty()) {
            this.comment = comment;
        }
        this.occupation = new Occupation(occupationId);
    }

    public void updateInformation(ReviewUpdateDTO data) {
        if (data.comment() != null && !data.comment().trim().isEmpty()) {
            this.comment = data.comment();
        }
    }
}
