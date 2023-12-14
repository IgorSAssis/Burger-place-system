package br.com.burger_place_app.controller;

import br.com.burger_place_app.domain.product.ProductService;
import br.com.burger_place_app.domain.product.dto.CreateProductData;
import br.com.burger_place_app.domain.product.dto.ProductData;
import br.com.burger_place_app.domain.product.dto.UpdateProductData;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
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
    public ResponseEntity listProducts(Pageable pageable) {
        return ResponseEntity.ok(this.productService.listProducts(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity showProduct(
            @PathVariable
            Long id
    ) {
        Optional<ProductData> productOptional = this.productService.showProduct(id);

        if (productOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(productOptional.get());
    }

    @PostMapping
    @Transactional
    public ResponseEntity createProduct(
            @RequestBody
            @Valid
            CreateProductData productData,
            UriComponentsBuilder uriComponentsBuilder
    ) {
        ProductData product = this.productService.createProduct(productData);

        URI uri = uriComponentsBuilder
                .path("/pets/{id}")
                .buildAndExpand(product.id())
                .toUri();

        return ResponseEntity.created(uri).body(product);
    }

    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity updateProduct(
            @PathVariable
            Long id,
            @RequestBody
            @Valid
            UpdateProductData productData
    ) {
        return ResponseEntity.ok(
                this.productService.updateProduct(id, productData)
        );
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity inactivateProduct(
            @PathVariable
            Long id
    ) {
        this.productService.deleteProduct(id);

        return ResponseEntity.noContent().build();
    }
}
