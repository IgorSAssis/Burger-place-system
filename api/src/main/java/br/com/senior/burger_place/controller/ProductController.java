package br.com.senior.burger_place.controller;

import br.com.senior.burger_place.domain.product.ProductCategory;
import br.com.senior.burger_place.domain.product.ProductService;
import br.com.senior.burger_place.domain.product.dto.CreateProductDTO;
import br.com.senior.burger_place.domain.product.dto.ProductDTO;
import br.com.senior.burger_place.domain.product.dto.UpdateProductDTO;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Optional;

@RestController
@RequestMapping("products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping
    public ResponseEntity<Page<ProductDTO>> listProducts(
            Pageable pageable,
            @RequestParam(name = "category", required = false)
            ProductCategory category
    ) {
        return ResponseEntity.ok(this.productService.listProducts(pageable, category));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> showProduct(
            @PathVariable
            Long id
    ) {
        Optional<ProductDTO> productOptional = this.productService.showProduct(id);

        return productOptional.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());

    }

    @PostMapping
    @Transactional
    public ResponseEntity<ProductDTO> createProduct(
            @RequestBody
            @Valid
            CreateProductDTO productData,
            UriComponentsBuilder uriComponentsBuilder
    ) {
        ProductDTO product = this.productService.createProduct(productData);

        URI uri = uriComponentsBuilder
                .path("/products/{id}")
                .buildAndExpand(product.id())
                .toUri();

        return ResponseEntity.created(uri).body(product);
    }

    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<ProductDTO> updateProduct(
            @PathVariable
            Long id,
            @RequestBody
            @Valid
            UpdateProductDTO productData
    ) {
        return ResponseEntity.ok(
                this.productService.updateProduct(id, productData)
        );
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<Void> inactivateProduct(
            @PathVariable
            Long id
    ) {
        this.productService.deleteProduct(id);

        return ResponseEntity.noContent().build();
    }
}
