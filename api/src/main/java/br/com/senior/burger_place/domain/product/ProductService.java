package br.com.senior.burger_place.domain.product;

import br.com.senior.burger_place.domain.product.dto.CreateProductDTO;
import br.com.senior.burger_place.domain.product.dto.UpdateProductDTO;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ProductSpecification productSpecification;

    public Page<Product> listProducts(
            Pageable pageable,
            String name,
            Double price,
            String ingredients,
            ProductCategory category,
            Boolean active
    ) {
        Specification<Product> specification = this.productSpecification.applyFilters(
                name,
                price,
                ingredients,
                category,
                active
        );

        return this.productRepository.findAll(specification, pageable);
    }

    public Product showProduct(
            @NonNull
            UUID productId
    ) {
        return this.productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product does not exist"));
    }

    @Transactional
    public Product createProduct(
            @Valid
            @NonNull
            CreateProductDTO dto
    ) {
        Product product = Product.builder()
                .name(dto.getName())
                .price(dto.getPrice())
                .ingredients(dto.getIngredients())
                .category(dto.getCategory())
                .url(dto.getUrl())
                .build();

        return this.productRepository.save(product);
    }

    @Transactional
    public Product updateProduct(
            @NonNull
            UUID productId,
            @NonNull
            @Valid
            UpdateProductDTO dto
    ) {
        Product product = this.findActiveProductById(productId);
        product.update(
                dto.getName(),
                dto.getIngredients(),
                dto.getPrice(),
                dto.getCategory(),
                dto.getUrl()
        );

        return product;
    }

    @Transactional
    public void inactivateProduct(
            @NonNull
            UUID productId
    ) {
        Product product = this.findActiveProductById(productId);
        product.inactivate();
    }

    private Product findActiveProductById(UUID productId) {
        return this.productRepository.findByIdAndActiveTrue(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product does not exists or is inactive"));
    }
}
