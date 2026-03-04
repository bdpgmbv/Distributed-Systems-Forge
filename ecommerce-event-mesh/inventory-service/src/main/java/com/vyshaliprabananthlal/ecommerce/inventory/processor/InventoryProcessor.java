package com.vyshaliprabananthlal.ecommerce.inventory.processor;

/**
 * 3/1/26 - 21:19
 *
 * @author Vyshali Prabananth Lal
 */

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class InventoryProcessor {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final JdbcTemplate jdbcTemplate;
    private final ObjectMapper objectMapper;

    public InventoryProcessor(KafkaTemplate<String, String> kafkaTemplate, JdbcTemplate jdbcTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.jdbcTemplate = jdbcTemplate;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "inventory-commands", groupId = "inventory-group")
    public void processInventory(String payload) throws Exception {
        // DLQ TRIGGER: If the Orchestrator sent bad JSON, this throws an Exception.
        // KafkaConfig will catch it, retry 3 times, and route it to the DLQ!
        ObjectNode command = (ObjectNode) objectMapper.readTree(payload);

        String orderId = command.get("orderId").asText();
        String productId = command.get("productId").asText();

        System.out.println("📦 Checking inventory for Product: " + productId);

        // Simple stock check logic
        Integer currentStock = jdbcTemplate.queryForObject("SELECT stock FROM inventory WHERE product_id = ?", Integer.class, productId);

        ObjectNode reply = objectMapper.createObjectNode();
        reply.put("orderId", orderId);

        if (currentStock != null && currentStock > 0) {
            jdbcTemplate.update("UPDATE inventory SET stock = stock - 1 WHERE product_id = ?", productId);
            reply.put("status", "INVENTORY_DEDUCTED");
            kafkaTemplate.send("inventory-events", orderId, reply.toString());
            System.out.println("✅ Stock deducted. 1 item shipped!");
        } else {
            reply.put("status", "OUT_OF_STOCK");
            kafkaTemplate.send("inventory-events", orderId, reply.toString());
            System.err.println("❌ Out of stock! Cannot fulfill order.");
        }
    }
}
