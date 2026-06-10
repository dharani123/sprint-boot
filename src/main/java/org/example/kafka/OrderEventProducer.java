package org.example.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.event.OrderPlacedEvent;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

/**
 * Publishes order events to Kafka.
 *
 * {@link KafkaTemplate} is Spring's helper for sending messages — think of it like
 * {@code RestTemplate}/{@code JdbcTemplate} but for Kafka. Spring Boot auto-configures
 * it from the {@code spring.kafka.producer.*} properties.
 */
@Slf4j
@Service
// Only create this bean when app.kafka.enabled is true (or absent → defaults to on).
@ConditionalOnProperty(name = "app.kafka.enabled", havingValue = "true", matchIfMissing = true)
@RequiredArgsConstructor
public class OrderEventProducer {

    // <String, OrderPlacedEvent> = <key type, value type>
    private final KafkaTemplate<String, OrderPlacedEvent> kafkaTemplate;

    public void publishOrderPlaced(OrderPlacedEvent event) {
        log.info("→ publishing OrderPlacedEvent to topic '{}': orderId={}, user={}",
                KafkaTopicConfig.ORDER_PLACED_TOPIC, event.getOrderId(), event.getUserEmail());

        // Key = userEmail. Kafka routes all messages with the same key to the same
        // partition, which guarantees a single user's orders stay in the order they were sent.
        kafkaTemplate.send(KafkaTopicConfig.ORDER_PLACED_TOPIC, event.getUserEmail(), event);

        log.info("← published OrderPlacedEvent: orderId={}", event.getOrderId());
    }
}
