package org.example.controller;

import org.example.dto.CartItemRequest;
import org.example.dto.CheckoutResponse;
import org.example.model.Product;
import org.example.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

@Slf4j
@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final ProductService productService;

    public CartController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping("/checkout")
    public ResponseEntity<?> checkout(@RequestBody List<CartItemRequest> cartItems) {
        if (cartItems == null || cartItems.isEmpty()) {
            log.warn("Checkout attempted with empty cart");
            return ResponseEntity.badRequest().body(Map.of("error", "Cart is empty"));
        }

        int totalQty = cartItems.stream().mapToInt(CartItemRequest::getQuantity).sum();
        log.info("Checkout request: {} line items, {} total units", cartItems.size(), totalQty);

        List<CheckoutResponse.LineItem> lineItems = new ArrayList<>();
        double subtotal = 0;

        for (CartItemRequest item : cartItems) {
            Optional<Product> found = productService.getProductById(item.getProductId());
            if (found.isEmpty()) {
                log.warn("Checkout failed — product not found: id={}", item.getProductId());
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Product not found: " + item.getProductId()));
            }
            Product p = found.get();
            double itemTotal = p.getPrice() * item.getQuantity();
            lineItems.add(CheckoutResponse.LineItem.builder()
                    .productId(p.getId())
                    .name(p.getName())
                    .emoji(p.getEmoji())
                    .quantity(item.getQuantity())
                    .unitPrice(p.getPrice())
                    .itemTotal(itemTotal)
                    .build());
            subtotal += itemTotal;
        }

        double deliveryFee = subtotal >= 199 ? 0 : 25;
        double platformFee = 3;
        double discount    = 0;
        double total       = subtotal + deliveryFee + platformFee - discount;
        String orderId     = "ORD-" + (10000 + new Random().nextInt(90000));

        log.info("Order {} created: subtotal=₹{}, delivery=₹{}, total=₹{}", orderId, subtotal, deliveryFee, total);

        return ResponseEntity.status(201).body(
                CheckoutResponse.builder()
                        .orderId(orderId)
                        .items(lineItems)
                        .subtotal(subtotal)
                        .deliveryFee(deliveryFee)
                        .platformFee(platformFee)
                        .discount(discount)
                        .total(total)
                        .build()
        );
    }
}
