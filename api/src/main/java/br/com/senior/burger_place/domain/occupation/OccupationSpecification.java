package br.com.senior.burger_place.domain.occupation;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class OccupationSpecification {
    private Specification<Occupation> filterByBeginOccupation(LocalDateTime beginOccupation) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.greaterThanOrEqualTo(root.get("beginOccupation"), beginOccupation);
    }

    private Specification<Occupation> filterByEndOccupation(LocalDateTime endOccupation) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.lessThanOrEqualTo(root.get("endOccupation"), endOccupation);
    }

    private Specification<Occupation> filterByPaymentForm(PaymentForm paymentForm) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("paymentForm"), paymentForm);
    }

    private Specification<Occupation> filterByPeopleCount(Integer peopleCount) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("peopleCount"), peopleCount);
    }

    private Specification<Occupation> filterByBoardNumber(Integer boardNumber) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("board ").get("number"), boardNumber);
    }

    private Specification<Occupation> filterByActive(Boolean active) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("active"), active);
    }

    public Specification<Occupation> applyFilters(
            LocalDateTime beginOccupation,
            LocalDateTime endOccupation,
            PaymentForm paymentForm,
            Integer peopleCount,
            Integer boardNumber,
            Boolean active
    ) {
        Specification<Occupation> specification = Specification.where(null);

        if (beginOccupation != null) {
            specification = specification.and(this.filterByBeginOccupation(beginOccupation));
        }

        if (endOccupation != null) {
            specification = specification.and(this.filterByEndOccupation(endOccupation));
        }

        if (paymentForm != null) {
            specification = specification.and(this.filterByPaymentForm(paymentForm));
        }

        if (peopleCount != null) {
            specification = specification.and(this.filterByPeopleCount(peopleCount));
        }

        if (boardNumber != null) {
            specification = specification.and(this.filterByBoardNumber(boardNumber));
        }

        if (active != null) {
            specification = specification.and(this.filterByActive(active));
        }

        return specification;
    }
}
