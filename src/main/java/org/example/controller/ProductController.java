package org.example.controller;

import org.example.model.Product;
import org.example.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")  // All endpoints in this class start with /api/products
public class ProductController {

    // Spring INJECTS the ProductService here automatically - we never call "new ProductService()"
    // This is called Dependency Injection (DI) - Concept 6!
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // GET /api/products  → returns all products as JSON array
    @GetMapping
    public List<Product> getAllProducts() {
        return productService.getAllProducts();
    }

    // GET /api/products/1  → returns single product or 404
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable int id) {
        return productService.getProductById(id)
                .map(ResponseEntity::ok)                        // found → 200 OK
                .orElse(ResponseEntity.notFound().build());     // not found → 404
    }

    // POST /api/products  → creates a new product from JSON body
    @PostMapping
    public ResponseEntity<Product> addProduct(@RequestBody Product product) {
        Product created = productService.addProduct(product);
        return ResponseEntity.status(201).body(created);        // 201 Created
    }

    // PUT /api/products/1  → updates an existing product
    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable int id, @RequestBody Product product) {
        return productService.updateProduct(id, product)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // DELETE /api/products/1  → removes a product
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable int id) {
        boolean deleted = productService.deleteProduct(id);
        return deleted ? ResponseEntity.noContent().build()     // 204 No Content
                       : ResponseEntity.notFound().build();     // 404
    }
}
