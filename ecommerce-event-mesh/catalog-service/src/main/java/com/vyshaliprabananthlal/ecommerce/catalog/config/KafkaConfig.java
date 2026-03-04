package com.vyshaliprabananthlal.ecommerce.catalog.config;

/**
 * 3/1/26 - 15:42
 *
 * @author Vyshali Prabananth Lal
 */

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {

    // This tells Spring to ensure a topic named "product-events" exists in our KRaft broker
    @Bean
    public NewTopic productEventsTopic() {
        return TopicBuilder.name("product-events").partitions(3) // Good practice for allowing multiple consumers later
                .replicas(1)   // We only have 1 Kafka broker running in Docker
                .build();
    }
}
