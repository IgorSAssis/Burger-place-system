package br.com.senior.burger_place.domain.occupation;

import org.springframework.data.jpa.domain.Specification;

public class OrderSpecification {

    public static Specification<Occupation> filterByOrderStatus(OrderItemStatus orderItemStatusParam) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("status"), orderItemStatusParam);
    }

}
