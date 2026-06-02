package org.example.service;

import lombok.extern.slf4j.Slf4j;
import org.example.model.Order;
import org.example.model.Product;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
public class OrderService {

    private final ProductService productService;

    public OrderService(ProductService productService) {
        this.productService = productService;
    }

    private final List<Order> orders = new ArrayList<>();
    private final AtomicInteger idCounter = new AtomicInteger(1);

    public List<Order> getAllOrders() {
        log.info("  [OrderService] getAllOrders() ─ entry");
        log.info("    [store] reading in-memory orders list, size={}", orders.size());
        log.info("  [OrderService] getAllOrders() ─ exit → {} orders", orders.size());
        return orders;
    }

    public Optional<Order> placeOrder(int productId, int quantity) {
        log.info("  [OrderService] placeOrder(productId={}, quantity={}) ─ entry", productId, quantity);

        log.info("    [DI] delegating to productService.getProductById({}) — cross-service call via DI", productId);
        Optional<Product> found = productService.getProductById(productId);

        if (found.isEmpty()) {
            log.warn("  [OrderService] placeOrder() ─ exit → product {} not found, returning Optional.empty", productId);
            return Optional.empty();
        }

        Product product = found.get();
        double total = product.getPrice() * quantity;
        log.info("    [calc] price=₹{} × qty={} = total=₹{}", product.getPrice(), quantity, total);

        Order order = new Order(idCounter.getAndIncrement(), product.getId(), product.getName(), quantity, total);
        log.info("    [store] adding order to in-memory list: orderId={}, product='{}', total=₹{}", order.getOrderId(), order.getProductName(), total);
        orders.add(order);

        log.info("  [OrderService] placeOrder() ─ exit → Order created: id={}", order.getOrderId());
        return Optional.of(order);
    }

    public String getSummary() {
        log.info("  [OrderService] getSummary() ─ entry");

        log.info("    [DI] delegating to productService.getAllProducts() — cross-service call via DI");
        List<Product> products = productService.getAllProducts();
        log.info("    [DI] productService.getAllProducts() returned {} products", products.size());

        int totalOrders = orders.size();
        double revenue  = orders.stream().mapToDouble(Order::getTotalPrice).sum();
        log.info("    [calc] totalOrders={}, revenue=₹{}", totalOrders, revenue);

        String summary = String.format("Catalog: %d products | Orders placed: %d | Total revenue: ₹%.2f",
                products.size(), totalOrders, revenue);
        log.info("  [OrderService] getSummary() ─ exit → '{}'", summary);
        return summary;
    }
}
