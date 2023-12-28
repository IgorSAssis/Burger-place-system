package br.com.senior.burger_place.domain.product;

import br.com.senior.burger_place.domain.product.dto.CreateProductDTO;
import br.com.senior.burger_place.domain.product.dto.ProductDTO;
import br.com.senior.burger_place.domain.product.dto.UpdateProductDTO;
import br.com.senior.burger_place.domain.product.validation.NonExistentProductValidation;
import br.com.senior.burger_place.domain.product.validation.ProductDTOFieldsValidation;
import br.com.senior.burger_place.domain.validation.InvalidDTOValidation;
import br.com.senior.burger_place.domain.validation.InvalidIdValidation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;

    public Page<ProductDTO> listProducts(Pageable pageable) {
        return this.productRepository.findAllByActiveTrue(pageable).map(ProductDTO::new);
    }

    public Optional<ProductDTO> showProduct(Long id) {
        InvalidIdValidation.validate(id);

        Product product = this.productRepository.getReferenceByIdAndActiveTrue(id);

        if (product == null) {
            return Optional.empty();
        }

        return Optional.of(new ProductDTO(product));
    }

    public ProductDTO createProduct(CreateProductDTO productData) {
        InvalidDTOValidation.validate(productData);
        ProductDTOFieldsValidation.validate(productData.name(), productData.price());

        Product product = new Product(
                productData.name(),
                productData.price(),
                productData.description()
        );

        return new ProductDTO(this.productRepository.save(product));
    }

    public ProductDTO updateProduct(Long id, UpdateProductDTO productData) {
        InvalidIdValidation.validate(id);
        InvalidDTOValidation.validate(productData);
        ProductDTOFieldsValidation.validate(productData.name(), productData.price());

        Product product = this.productRepository.getReferenceByIdAndActiveTrue(id);

        NonExistentProductValidation.validate(product);

        product.update(productData);
        return new ProductDTO(product);
    }

    public void deleteProduct(Long id) {
        InvalidIdValidation.validate(id);

        Product product = this.productRepository.getReferenceByIdAndActiveTrue(id);

        NonExistentProductValidation.validate(product);

        product.inactivate();
    }
}
