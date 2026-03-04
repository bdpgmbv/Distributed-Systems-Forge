package com.vyshaliprabananthlal.ecommerce.catalog.repository;

/**
 * 3/1/26 - 15:35
 *
 * @author Vyshali Prabananth Lal
 */

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vyshaliprabananthlal.ecommerce.catalog.domain.Product;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Repository
public class CatalogRepository {

    private final JdbcTemplate jdbcTemplate;
    private final ObjectMapper objectMapper;

    // Spring automatically injects these tools for us
    public CatalogRepository(JdbcTemplate jdbcTemplate, ObjectMapper objectMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.objectMapper = objectMapper;
    }

    /**
     * @Transactional is the most important part here!
     * It guarantees that both INSERT statements succeed, or BOTH fail.
     */
    @Transactional
    public void saveProductAndEvent(Product product) {
        try {
            // 1. Save the actual product state
            String insertProductSql = "INSERT INTO products (id, name, price) VALUES (?, ?, ?)";
            jdbcTemplate.update(insertProductSql, product.id(), product.name(), product.price());

            // 2. Convert the product into a JSON string to act as our Event Payload
            String eventPayload = objectMapper.writeValueAsString(product);

            // 3. Save the Event to the Outbox table
            String insertOutboxSql = "INSERT INTO outbox_events (event_id, aggregate_id, event_type, payload) VALUES (?, ?, ?, ?)";
            jdbcTemplate.update(insertOutboxSql, UUID.randomUUID(), product.id(), "ProductCreated", // The type of event downstream consumers will look for
                    eventPayload);

            System.out.println(" Successfully saved product and outbox event for: " + product.name());

        } catch (Exception e) {
            throw new RuntimeException("Database transaction failed, rolling back!", e);
        }
    }
}
