package org.example.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.model.Order;
import org.example.service.OrderService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping
    public List<Order> getMyOrders() {
        String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        log.info("→ GET /api/orders user={}", email);
        return orderService.getOrdersForUser(email);
    }

    @GetMapping("/summary")
    public Map<String, String> getSummary() {
        return Map.of("summary", orderService.getSummary());
    }
}
