package org.example.event;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * The message we publish to Kafka every time an order is placed.
 *
 * This is a flat, self-contained snapshot of the order + who placed it.
 * It is deliberately separate from the JPA {@code Order} entity: the event is a
 * "contract" sent over the wire, so it should not drag along JPA/Hibernate concerns.
 *
 * Jackson serializes this to JSON on the way out and back into an object on the way in.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderPlacedEvent {

    private Long orderId;

    // Who placed the order — carried in the event so the consumer needs no DB lookup
    private String userEmail;
    private String userName;

    private String status;
    private double subtotal;
    private double deliveryFee;
    private double platformFee;
    private double total;
    private LocalDateTime createdAt;

    private List<Item> items;

    /** One line of the order. Nested because it only ever exists inside an event. */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Item {
        private Long   productId;
        private String productName;
        private String emoji;
        private int    quantity;
        private double unitPrice;
        private double itemTotal;
    }
}
