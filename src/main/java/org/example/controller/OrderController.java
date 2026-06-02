package org.example.controller;

import lombok.extern.slf4j.Slf4j;
import org.example.model.Order;
import org.example.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public List<Order> getAllOrders() {
        log.info("→ [OrderController] GET /api/orders");
        List<Order> result = orderService.getAllOrders();
        log.info("← [OrderController] GET /api/orders → returning {} orders", result.size());
        return result;
    }

    @PostMapping
    public ResponseEntity<?> placeOrder(@RequestBody Map<String, Integer> request) {
        int productId = request.getOrDefault("productId", 0);
        int quantity  = request.getOrDefault("quantity", 1);
        log.info("→ [OrderController] POST /api/orders, productId={}, quantity={}", productId, quantity);

        return orderService.placeOrder(productId, quantity)
                .<ResponseEntity<?>>map(order -> {
                    log.info("← [OrderController] POST /api/orders → 201 Created, orderId={}, total=₹{}", order.getOrderId(), order.getTotalPrice());
                    return ResponseEntity.status(201).body(order);
                })
                .orElseGet(() -> {
                    log.warn("← [OrderController] POST /api/orders → 400 Bad Request, productId={} not found", productId);
                    return ResponseEntity.badRequest().body(Map.of("error", "Product with id " + productId + " not found"));
                });
    }

    @GetMapping("/summary")
    public Map<String, String> getSummary() {
        log.info("→ [OrderController] GET /api/orders/summary");
        String summary = orderService.getSummary();
        log.info("← [OrderController] GET /api/orders/summary → '{}'", summary);
        return Map.of("summary", summary);
    }
}
