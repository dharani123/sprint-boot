package org.example.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.model.Product;
import org.example.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public List<Product> getAllProducts() {
        log.info("→ entering getAllProducts");
        List<Product> products = productRepository.findAll();
        log.info("← returning getAllProducts: {} products", products.size());
        return products;
    }

    public Optional<Product> getProductById(Long id) {
        log.info("→ entering getProductById: id={}", id);
        Optional<Product> result = productRepository.findById(id);
        log.info("← returning getProductById: {}", result.isPresent() ? "found" : "not found");
        return result;
    }

    public Product addProduct(Product product) {
        log.info("→ entering addProduct: name={}", product.getName());
        Product saved = productRepository.save(product);
        log.info("← returning addProduct: id={}", saved.getId());
        return saved;
    }

    public Optional<Product> updateProduct(Long id, Product updated) {
        log.info("→ entering updateProduct: id={}", id);
        return productRepository.findById(id).map(existing -> {
            existing.setName(updated.getName());
            existing.setPrice(updated.getPrice());
            existing.setCategory(updated.getCategory());
            existing.setEmoji(updated.getEmoji());
            existing.setStock(updated.getStock());
            existing.setUnitLabel(updated.getUnitLabel());
            Product saved = productRepository.save(existing);
            log.info("← returning updateProduct: updated id={}", id);
            return saved;
        });
    }

    public boolean deleteProduct(Long id) {
        log.info("→ entering deleteProduct: id={}", id);
        if (!productRepository.existsById(id)) {
            log.info("← returning deleteProduct: not found id={}", id);
            return false;
        }
        productRepository.deleteById(id);
        log.info("← returning deleteProduct: deleted id={}", id);
        return true;
    }
}
