package org.example.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.CartItemRequest;
import org.example.model.Order;
import org.example.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final OrderService orderService;

    @PostMapping("/checkout")
    public ResponseEntity<?> checkout(@RequestBody List<CartItemRequest> cartItems) {
        if (cartItems == null || cartItems.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Cart is empty"));
        }

        // JwtFilter already validated the token and stored the email in the SecurityContext
        String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        log.info("→ checkout: {} items, user={}", cartItems.size(), email);

        return orderService.checkout(cartItems, email)
                .<ResponseEntity<?>>map(order -> {
                    log.info("← checkout: orderId={}, total=₹{}", order.getId(), order.getTotal());
                    return ResponseEntity.status(201).body(Map.of(
                        "orderId",     order.getId(),
                        "items",       order.getItems(),
                        "subtotal",    order.getSubtotal(),
                        "deliveryFee", order.getDeliveryFee(),
                        "platformFee", order.getPlatformFee(),
                        "discount",    0,
                        "total",       order.getTotal()
                    ));
                })
                .orElseGet(() -> ResponseEntity.badRequest().body(Map.of("error", "One or more products not found")));
    }
}
