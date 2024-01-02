package br.com.senior.burger_place.domain.product;

import org.springframework.data.jpa.domain.Specification;

public class ProductSpecification {

    public static Specification<Product> filterByCategory(ProductCategory category) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("category"), category);
    }

    public static Specification<Product> filterByActive() {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.isTrue(root.get("active"));
    }

    public static Specification<Product> apply(ProductCategory category) {
        Specification<Product> specification = Specification.where(null);

        if (category != null) {
            specification = specification.and(filterByCategory(category));
        }

        specification = specification.and(filterByActive());

        return specification;
    }

}
