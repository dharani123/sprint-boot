package org.example.controller;

import org.example.model.Order;
import org.example.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    // Spring injects OrderService here
    // OrderService itself has ProductService injected inside it
    // Spring builds the whole chain: Controller ← Service ← Service
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    // GET /api/orders
    @GetMapping
    public List<Order> getAllOrders() {
        return orderService.getAllOrders();
    }

    // POST /api/orders  body: { "productId": 1, "quantity": 2 }
    @PostMapping
    public ResponseEntity<?> placeOrder(@RequestBody Map<String, Integer> request) {
        int productId = request.getOrDefault("productId", 0);
        int quantity  = request.getOrDefault("quantity", 1);

        return orderService.placeOrder(productId, quantity)
                .<ResponseEntity<?>>map(order -> ResponseEntity.status(201).body(order))
                .orElse(ResponseEntity.badRequest().body(
                    Map.of("error", "Product with id " + productId + " not found")
                ));
    }

    // GET /api/orders/summary  — shows DI enabling cross-service data
    @GetMapping("/summary")
    public Map<String, String> getSummary() {
        return Map.of("summary", orderService.getSummary());
    }
}
