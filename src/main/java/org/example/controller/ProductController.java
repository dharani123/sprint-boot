package org.example.controller;

import lombok.extern.slf4j.Slf4j;
import org.example.model.Product;
import org.example.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public List<Product> getAllProducts() {
        log.info("→ [ProductController] GET /api/products");
        List<Product> result = productService.getAllProducts();
        log.info("← [ProductController] GET /api/products → returning {} products", result.size());
        return result;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        log.info("→ [ProductController] GET /api/products/{}", id);
        ResponseEntity<Product> response = productService.getProductById(id)
                .map(p -> {
                    log.info("← [ProductController] GET /api/products/{} → 200 OK, name='{}'", id, p.getName());
                    return ResponseEntity.ok(p);
                })
                .orElseGet(() -> {
                    log.warn("← [ProductController] GET /api/products/{} → 404 Not Found", id);
                    return ResponseEntity.notFound().<Product>build();
                });
        return response;
    }

    @PostMapping
    public ResponseEntity<Product> addProduct(@RequestBody Product product) {
        log.info("→ [ProductController] POST /api/products, body: name='{}', price={}", product.getName(), product.getPrice());
        Product created = productService.addProduct(product);
        log.info("← [ProductController] POST /api/products → 201 Created, id={}", created.getId());
        return ResponseEntity.status(201).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable Long id, @RequestBody Product product) {
        log.info("→ [ProductController] PUT /api/products/{}, new name='{}', price={}", id, product.getName(), product.getPrice());
        ResponseEntity<Product> response = productService.updateProduct(id, product)
                .map(p -> {
                    log.info("← [ProductController] PUT /api/products/{} → 200 OK", id);
                    return ResponseEntity.ok(p);
                })
                .orElseGet(() -> {
                    log.warn("← [ProductController] PUT /api/products/{} → 404 Not Found", id);
                    return ResponseEntity.notFound().<Product>build();
                });
        return response;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        log.info("→ [ProductController] DELETE /api/products/{}", id);
        boolean deleted = productService.deleteProduct(id);
        if (deleted) {
            log.info("← [ProductController] DELETE /api/products/{} → 204 No Content", id);
            return ResponseEntity.noContent().build();
        } else {
            log.warn("← [ProductController] DELETE /api/products/{} → 404 Not Found", id);
            return ResponseEntity.notFound().build();
        }
    }
}
