package br.com.senior.burger_place.domain.review;

import br.com.senior.burger_place.domain.occupation.Occupation;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.NoSuchElementException;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Table(name = "reviews")
@Entity(name = "Review")
@JsonIgnoreProperties({"hibernateLazyInitializer"})
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long id;
    private Integer grade;
    private String comment;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "occupation_id")
    @JsonIgnoreProperties({"beginOccupation", "endOccupation", "peopleCount", "paymentForm", "orderItems", "board", "customers", "active"})
    private Occupation occupation;

    public Review(Long occupationId, ReviewRegisterData data) {
        if (data.grade() < 0 || data.grade() > 5){
            throw new NoSuchElementException("A nota deve ser entre 0 e 5");
        }
        this.grade = data.grade();
        this.comment = data.comment();
        this.occupation = new Occupation(occupationId);
    }

    public void updateInformation(ReviewUpdateData data) {
        if (data.grade() < 0 || data.grade() > 5){
            throw new NoSuchElementException("A nota deve ser entre 0 e 5");
        }
        if (data.grade() != null){
            this.grade = data.grade();
        }
        if (data.comment() != null){
            this.comment = data.comment();
        }
    }
}
