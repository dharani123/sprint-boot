package org.example.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.CartItemRequest;
import org.example.event.OrderPlacedEvent;
import org.example.kafka.OrderEventProducer;
import org.example.model.Order;
import org.example.model.OrderItem;
import org.example.model.Product;
import org.example.model.User;
import org.example.repository.OrderRepository;
import org.example.repository.UserRepository;
import org.springframework.beans.factory.ObjectProvider;
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

    // ObjectProvider lets us depend on the producer WITHOUT requiring it to exist.
    // When app.kafka.enabled=false the producer bean is absent, and this stays empty.
    private final ObjectProvider<OrderEventProducer> orderEventProducer;

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

        // ── Announce the order to Kafka ──────────────────────────────────────
        // The order is already safely in the DB. We now publish an event so other
        // parts of the system can react on their own. Phase 1: a consumer just prints
        // it. Phase 2: a consumer will index it into Elasticsearch.
        // ifAvailable runs the lambda ONLY when the producer bean exists (Kafka enabled).
        // When Kafka is disabled, checkout still works exactly the same — minus the event.
        orderEventProducer.ifAvailable(producer -> producer.publishOrderPlaced(toEvent(saved, user)));

        log.info("← returning checkout: orderId={}, user={}, total=₹{}", saved.getId(), userEmail, total);
        return Optional.of(saved);
    }

    // Maps the persisted Order + its owner into the flat event we publish to Kafka.
    private OrderPlacedEvent toEvent(Order order, User user) {
        List<OrderPlacedEvent.Item> items = order.getItems().stream()
                .map(oi -> OrderPlacedEvent.Item.builder()
                        .productId(oi.getProductId())
                        .productName(oi.getProductName())
                        .emoji(oi.getEmoji())
                        .quantity(oi.getQuantity())
                        .unitPrice(oi.getUnitPrice())
                        .itemTotal(oi.getItemTotal())
                        .build())
                .toList();

        return OrderPlacedEvent.builder()
                .orderId(order.getId())
                .userEmail(user.getEmail())
                .userName(user.getName())
                .status(order.getStatus())
                .subtotal(order.getSubtotal())
                .deliveryFee(order.getDeliveryFee())
                .platformFee(order.getPlatformFee())
                .total(order.getTotal())
                .createdAt(order.getCreatedAt())
                .items(items)
                .build();
    }

    public String getSummary() {
        long totalOrders = orderRepository.count();
        double revenue   = orderRepository.findAll().stream().mapToDouble(Order::getTotal).sum();
        int productCount = productService.getAllProducts().size();
        return String.format("Catalog: %d products | Orders placed: %d | Total revenue: ₹%.2f",
                productCount, totalOrders, revenue);
    }
}
