package org.example.service;

import org.example.model.Product;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

// @Service tells Spring: "manage this class, make it available for injection"
// Spring creates ONE instance of this and reuses it everywhere (Singleton)
@Service
public class ProductService {

    // In-memory store - acts like a tiny database for now
    private final List<Product> products = new ArrayList<>();
    private final AtomicInteger idCounter = new AtomicInteger(1);

    // Pre-load some sample data when the service is created
    public ProductService() {
        products.add(new Product(idCounter.getAndIncrement(), "Laptop",  75000));
        products.add(new Product(idCounter.getAndIncrement(), "Phone",   25000));
        products.add(new Product(idCounter.getAndIncrement(), "Headphones", 3000));
    }

    public List<Product> getAllProducts() {
        return products;
    }

    public Optional<Product> getProductById(int id) {
        return products.stream()
                .filter(p -> p.getId() == id)
                .findFirst();
    }

    public Product addProduct(Product product) {
        product.setId(idCounter.getAndIncrement());
        products.add(product);
        return product;
    }

    public Optional<Product> updateProduct(int id, Product updated) {
        return getProductById(id).map(existing -> {
            existing.setName(updated.getName());
            existing.setPrice(updated.getPrice());
            return existing;
        });
    }

    public boolean deleteProduct(int id) {
        return products.removeIf(p -> p.getId() == id);
    }
}
