package br.com.senior.burger_place.controller;

import br.com.senior.burger_place.domain.product.ProductCategory;
import br.com.senior.burger_place.domain.product.ProductConverter;
import br.com.senior.burger_place.domain.product.ProductService;
import br.com.senior.burger_place.domain.product.dto.CreateProductDTO;
import br.com.senior.burger_place.domain.product.dto.ProductDTO;
import br.com.senior.burger_place.domain.product.dto.UpdateProductDTO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("products")
public class ProductController {
    @Autowired
    private ProductService productService;
    @Autowired
    private ProductConverter productConverter;

    @GetMapping()
    public ResponseEntity<Page<ProductDTO>> list(
            @RequestParam(name = "name", required = false)
            String name,
            @RequestParam(name = "price", required = false)
            Double price,
            @RequestParam(name = "ingredients", required = false)
            String ingredients,
            @RequestParam(name = "category", required = false)
            ProductCategory category,
            @RequestParam(name = "active", required = false)
            Boolean active,
            Pageable pageable
    ) {
        Page<ProductDTO> productDTOS = this.productService
                .listProducts(pageable, name, price, ingredients, category, active)
                .map(this.productConverter::toProductDTO);

        return ResponseEntity.ok(productDTOS);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> show(
            @PathVariable
            UUID id
    ) {
        return ResponseEntity.ok(
                this.productConverter.toProductDTO(this.productService.showProduct(id))
        );
    }

    @PostMapping
    public ResponseEntity<ProductDTO> create(
            @RequestBody
            @Valid
            CreateProductDTO dto,
            UriComponentsBuilder uriComponentsBuilder
    ) {
        ProductDTO productDTO = this.productConverter.toProductDTO(this.productService.createProduct(dto));

        URI uri = uriComponentsBuilder
                .path("/products/{id}")
                .buildAndExpand(productDTO.getId())
                .toUri();

        return ResponseEntity.created(uri).body(productDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductDTO> update(
            @PathVariable
            UUID id,
            @RequestBody
            @Valid
            UpdateProductDTO productData
    ) {
        return ResponseEntity.ok(
                this.productConverter.toProductDTO(this.productService.updateProduct(id, productData))
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> inactivate(
            @PathVariable
            UUID id
    ) {
        this.productService.inactivateProduct(id);

        return ResponseEntity.noContent().build();
    }
}
