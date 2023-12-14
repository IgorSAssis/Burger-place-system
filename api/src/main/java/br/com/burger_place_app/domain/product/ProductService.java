package br.com.burger_place_app.domain.product;

import br.com.burger_place_app.domain.product.dto.CreateProductData;
import br.com.burger_place_app.domain.product.dto.ProductData;
import br.com.burger_place_app.domain.product.dto.UpdateProductData;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;

    public Page<ProductData> listProducts(Pageable pageable) {
        return this.productRepository.findAllByActiveTrue(pageable).map(ProductData::new);
    }

    public Optional<ProductData> showProduct(Long id) {
        Product product = this.productRepository.getReferenceByIdAndActiveTrue(id);

        if (product == null) {
            return Optional.empty();
        }

        return Optional.of(new ProductData(product));
    }

    public ProductData createProduct(CreateProductData productData) {
        Product product = new Product(
                productData.name(),
                productData.price(),
                productData.description()
        );

        this.productRepository.save(product);
        return new ProductData(product);
    }

    public ProductData updateProduct(Long id, UpdateProductData productData) {
        Product product = this.productRepository.getReferenceByIdAndActiveTrue(id);

        if (product == null) {
            throw new EntityNotFoundException("Produto não existe ou foi inativado");
        }

        product.update(productData);
        return new ProductData(product);
    }

    public void deleteProduct(Long id) {
        Product product = this.productRepository.getReferenceByIdAndActiveTrue(id);

        if (product == null) {
            throw new EntityNotFoundException("Produto não existe ou foi inativado");
        }

        product.inactivate();
    }
}
