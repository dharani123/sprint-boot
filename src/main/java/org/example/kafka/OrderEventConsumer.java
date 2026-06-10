package org.example.kafka;

import lombok.extern.slf4j.Slf4j;
import org.example.event.OrderPlacedEvent;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Listens to the order-placed topic and (for now) just prints what it receives.
 *
 * The @KafkaListener annotation tells Spring to start a background thread that
 * continuously polls the topic. Each message is deserialized from JSON back into
 * an OrderPlacedEvent and handed to this method.
 *
 * PHASE 2 will change this method to index the event into Elasticsearch instead.
 */
@Slf4j
@Component
// When app.kafka.enabled is false this bean is skipped, so no @KafkaListener
// container starts and the app never tries to poll the broker.
@ConditionalOnProperty(name = "app.kafka.enabled", havingValue = "true", matchIfMissing = true)
public class OrderEventConsumer {

    @KafkaListener(
            topics  = KafkaTopicConfig.ORDER_PLACED_TOPIC,
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void onOrderPlaced(OrderPlacedEvent event) {
        log.info("📦 [KAFKA CONSUMER] order received from topic '{}'", KafkaTopicConfig.ORDER_PLACED_TOPIC);
        log.info("   orderId = {}", event.getOrderId());
        log.info("   user    = {} <{}>", event.getUserName(), event.getUserEmail());
        log.info("   status  = {}", event.getStatus());
        log.info("   total   = ₹{} (subtotal ₹{} + delivery ₹{} + platform ₹{})",
                event.getTotal(), event.getSubtotal(), event.getDeliveryFee(), event.getPlatformFee());
        event.getItems().forEach(i ->
                log.info("     - {} {}  x{}  = ₹{}", i.getEmoji(), i.getProductName(), i.getQuantity(), i.getItemTotal()));
    }
}
