package br.com.senior.burger_place.domain.product;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class ProductSpecification {
    private Specification<Product> filterByName(String name) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("name")),
                        "%" + name.toLowerCase(Locale.ROOT) + "%"
                );
    }

    private Specification<Product> filterByPrice(Double price) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("price"), price);
    }

    private Specification<Product> filterByIngredients(String ingredients) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("ingredients")),
                        "%" + ingredients.toLowerCase(Locale.ROOT) + "%"
                );
    }

    private Specification<Product> filterByCategory(ProductCategory category) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("category"), category);
    }

    private Specification<Product> filterByActive(Boolean active) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("active"), active);
    }

    public Specification<Product> applyFilters(
            String name,
            Double price,
            String ingredients,
            ProductCategory category,
            Boolean active
    ) {
        Specification<Product> specification = Specification.where(null);

        if (name != null) {
            specification = specification.and(this.filterByName(name));
        }

        if (price != null) {
            specification = specification.and(this.filterByPrice(price));
        }

        if (ingredients != null) {
            specification = specification.and(this.filterByIngredients(ingredients));
        }

        if (category != null) {
            specification = specification.and(this.filterByCategory(category));
        }

        if (active != null) {
            specification = specification.and(this.filterByActive(active));
        }

        return specification;
    }

}
