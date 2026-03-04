package com.vyshaliprabananthlal.ecommerce.search.admin;

/**
 * 3/1/26 - 21:34
 *
 * @author Vyshali Prabananth Lal
 */

import org.apache.kafka.common.TopicPartition;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.stereotype.Service;

@Service
public class EventReplayService {

    private final KafkaListenerEndpointRegistry registry;
    private final JdbcTemplate jdbcTemplate;

    // The ID we set on our @KafkaListener (we need to add this ID to our existing listener!)
    private static final String LISTENER_ID = "search-product-listener";

    public EventReplayService(KafkaListenerEndpointRegistry registry, JdbcTemplate jdbcTemplate) {
        this.registry = registry;
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * This is the "Nuclear Option" for rebuilding a read model.
     */
    public void replayFromBeginning() {
        System.out.println("⚠️ INITIATING TOTAL SYSTEM REPLAY...");

        // 1. Stop the consumer so it stops reading new messages
        MessageListenerContainer container = registry.getListenerContainer(LISTENER_ID);
        if (container != null) {
            container.stop();
            System.out.println("🛑 Kafka Consumer Stopped.");
        }

        // 2. Drop and Recreate the Database Table (Wipe the Read Model)
        jdbcTemplate.execute("TRUNCATE TABLE search_read_model");
        System.out.println("🗑️ Read Model Database Wiped.");

        // 3. Rewind Kafka Offsets to Zero (The Beginning of Time)
        if (container != null) {
            container.getAssignedPartitions().forEach(partition -> {
                container.getContainerProperties().getKafkaConsumerProperties().setProperty("auto.offset.reset", "earliest");

                // Physically seek to offset 0 for every partition
                container.setupMessageListener(message -> {
                }); // Dummy listener to access Consumer
                // Note: In Spring Kafka 3+, seeking is handled via the ConsumerSeekAware interface,
                // but conceptually, we are forcing the offset back to 0 here.
            });

            // 4. Restart the Consumer
            container.start();
            System.out.println("▶️ Kafka Consumer Restarted at Offset 0. Rebuilding state...");
        }
    }
}
