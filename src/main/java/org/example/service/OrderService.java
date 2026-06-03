package org.example.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.CartItemRequest;
import org.example.model.Order;
import org.example.model.OrderItem;
import org.example.model.Product;
import org.example.model.User;
import org.example.repository.OrderRepository;
import org.example.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final ProductService  productService;
    private final OrderRepository orderRepository;
    private final UserRepository  userRepository;

    public List<Order> getOrdersForUser(String email) {
        log.info("→ entering getOrdersForUser: email={}", email);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found: " + email));
        List<Order> orders = orderRepository.findByUserOrderByCreatedAtDesc(user);
        log.info("← returning getOrdersForUser: {} orders", orders.size());
        return orders;
    }

    @Transactional
    public Optional<Order> checkout(List<CartItemRequest> cartItems, String userEmail) {
        log.info("→ entering checkout: {} items, user={}", cartItems.size(), userEmail);

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found: " + userEmail));

        List<OrderItem> items = new ArrayList<>();
        double subtotal = 0;

        for (CartItemRequest cartItem : cartItems) {
            Optional<Product> found = productService.getProductById(cartItem.getProductId());
            if (found.isEmpty()) {
                log.warn("← checkout failed: product {} not found", cartItem.getProductId());
                return Optional.empty();
            }
            Product p = found.get();
            double itemTotal = p.getPrice() * cartItem.getQuantity();
            subtotal += itemTotal;

            items.add(OrderItem.builder()
                    .productId(p.getId())
                    .productName(p.getName())
                    .emoji(p.getEmoji())
                    .quantity(cartItem.getQuantity())
                    .unitPrice(p.getPrice())
                    .itemTotal(itemTotal)
                    .build());
        }

        double deliveryFee = subtotal >= 199 ? 0 : 25;
        double platformFee = 3;
        double total       = subtotal + deliveryFee + platformFee;

        Order order = Order.builder()
                .user(user)
                .status("CONFIRMED")
                .subtotal(subtotal)
                .deliveryFee(deliveryFee)
                .platformFee(platformFee)
                .total(total)
                .build();

        items.forEach(item -> item.setOrder(order));
        order.setItems(items);

        Order saved = orderRepository.save(order);
        log.info("← returning checkout: orderId={}, user={}, total=₹{}", saved.getId(), userEmail, total);
        return Optional.of(saved);
    }

    public String getSummary() {
        long totalOrders = orderRepository.count();
        double revenue   = orderRepository.findAll().stream().mapToDouble(Order::getTotal).sum();
        int productCount = productService.getAllProducts().size();
        return String.format("Catalog: %d products | Orders placed: %d | Total revenue: ₹%.2f",
                productCount, totalOrders, revenue);
    }
}
