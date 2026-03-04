package com.vyshaliprabananthlal.ecommerce.search.listener;

/**
 * 3/1/26 - 15:52
 *
 * @author Vyshali Prabananth Lal
 */

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vyshaliprabananthlal.ecommerce.search.domain.ProductEventDto;
import com.vyshaliprabananthlal.ecommerce.search.repository.SearchRepository;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class ProductEventListener {

    private final SearchRepository searchRepository;
    private final ObjectMapper objectMapper;

    public ProductEventListener(SearchRepository searchRepository, ObjectMapper objectMapper) {
        this.searchRepository = searchRepository;
        this.objectMapper = objectMapper;
    }

    /**
     * PATTERN: Change Data Capture (CDC) Consumer
     * Listens directly to the Debezium stream coming from the Postgres WAL
     */
    @KafkaListener(id = "search-product-listener", topics = "ecommerce_mesh.public.products", groupId = "search-group")
    public void consumeDebeziumCdcEvent(String rawJsonPayload) {
        try {
            JsonNode rootNode = objectMapper.readTree(rawJsonPayload);
            JsonNode payloadNode = rootNode.get("payload");

            if (payloadNode == null || payloadNode.isNull()) {
                return; // Tombstone message, safely ignore
            }

            // Extract the database operation type (c = create, u = update, d = delete)
            String operation = payloadNode.get("op").asText();

            if ("c".equals(operation) || "u".equals(operation)) {

                // Extract the row data from the "after" block
                JsonNode afterState = payloadNode.get("after");

                String id = afterState.get("id").asText();
                String name = afterState.get("name").asText();
                BigDecimal price = new BigDecimal(afterState.get("price").asText());

                // Save to our highly optimized Read Model
                ProductEventDto product = new ProductEventDto(id, name, price);
                searchRepository.upsertProductView(product);

                System.out.println("✅ CDC Processed! Read model updated for: " + name);

            } else if ("d".equals(operation)) {

                // Extract the row data from the "before" block because the row is gone now
                String id = payloadNode.get("before").get("id").asText();
                searchRepository.deleteProductView(id);

                System.out.println("🗑️ CDC Processed! Product deleted from read model: " + id);
            }

        } catch (Exception e) {
            System.err.println("❌ Failed to parse Debezium CDC payload.");
            throw new RuntimeException("Triggering DLQ routing for bad payload", e);
        }
    }

    /**
     * PATTERN: Dead Letter Queue (DLQ) Monitor
     * If the main listener throws an error 3 times, the message ends up here.
     */
    @KafkaListener(topics = "ecommerce_mesh.public.products.DLT", groupId = "search-dlq-group")
    public void consumeDlqEvent(ConsumerRecord<String, String> record) {
        System.err.println("🚨 POISON PILL DETECTED IN DLQ!");
        System.err.println("🚨 Failed Message Key: " + record.key());
        System.err.println("🚨 Failed Message Payload: " + record.value());

        // In reality, save this to an admin database table for manual review
    }
}