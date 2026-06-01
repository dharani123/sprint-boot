package org.example.service;

import org.example.model.Order;
import org.example.model.Product;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class OrderService {

    // ─────────────────────────────────────────────────────────────────
    // DEPENDENCY INJECTION in action:
    //
    // We do NOT write:  ProductService ps = new ProductService();
    //
    // Instead, Spring sees that OrderService needs a ProductService,
    // looks in its container, finds the one it already created,
    // and hands it to us via this constructor.
    //
    // Both OrderService and ProductService share the SAME instance.
    // ─────────────────────────────────────────────────────────────────
    private final ProductService productService;

    // Spring calls this constructor automatically and injects ProductService
    public OrderService(ProductService productService) {
        this.productService = productService;
    }

    private final List<Order> orders = new ArrayList<>();
    private final AtomicInteger idCounter = new AtomicInteger(1);

    public List<Order> getAllOrders() {
        return orders;
    }

    public Optional<Order> placeOrder(int productId, int quantity) {
        // Uses the INJECTED productService to look up the product
        return productService.getProductById(productId).map(product -> {
            double total = product.getPrice() * quantity;
            Order order = new Order(
                idCounter.getAndIncrement(),
                product.getId(),
                product.getName(),
                quantity,
                total
            );
            orders.add(order);
            return order;
        });
    }

    // Shows how DI enables reuse — OrderService leverages ProductService's logic
    public String getSummary() {
        List<Product> products = productService.getAllProducts();
        int totalOrders = orders.size();
        double revenue = orders.stream().mapToDouble(Order::getTotalPrice).sum();

        return String.format(
            "Catalog: %d products | Orders placed: %d | Total revenue: ₹%.2f",
            products.size(), totalOrders, revenue
        );
    }
}
