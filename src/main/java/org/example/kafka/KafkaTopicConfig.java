package org.example.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

/**
 * Declares the Kafka topics our app needs.
 *
 * Spring sees this {@link NewTopic} bean at startup and asks the broker to create
 * the topic if it doesn't already exist (via Kafka's AdminClient). That way we don't
 * have to create topics by hand on the command line.
 */
@Configuration
// No topic bean when Kafka is off → Spring's KafkaAdmin has nothing to create,
// so it won't reach out to the broker either.
@ConditionalOnProperty(name = "app.kafka.enabled", havingValue = "true", matchIfMissing = true)
public class KafkaTopicConfig {

    // One source of truth for the topic name — referenced by producer AND consumer.
    public static final String ORDER_PLACED_TOPIC = "order-placed";

    @Bean
    public NewTopic orderPlacedTopic() {
        return TopicBuilder.name(ORDER_PLACED_TOPIC)
                // 1 partition is fine for learning. More partitions = more parallelism.
                .partitions(1)
                // 1 replica because we run a single broker. In prod you'd use 3.
                .replicas(1)
                .build();
    }
}
