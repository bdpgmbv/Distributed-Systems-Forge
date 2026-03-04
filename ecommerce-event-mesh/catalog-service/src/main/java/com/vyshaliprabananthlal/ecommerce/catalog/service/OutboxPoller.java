package com.vyshaliprabananthlal.ecommerce.catalog.service;

/**
 * 3/1/26 - 15:43
 *
 * @author Vyshali Prabananth Lal
 */

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
public class OutboxPoller {

    private final JdbcTemplate jdbcTemplate;
    private final KafkaTemplate<String, String> kafkaTemplate;

    public OutboxPoller(JdbcTemplate jdbcTemplate, KafkaTemplate<String, String> kafkaTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.kafkaTemplate = kafkaTemplate;
    }

    // 1. We add @Transactional here so the Database operations act as one unit
    @Scheduled(fixedDelay = 2000)
    @Transactional
    public void pollAndPublish() {
        String selectSql = "SELECT event_id, aggregate_id, event_type, payload FROM outbox_events ORDER BY created_at ASC LIMIT 50 FOR UPDATE SKIP LOCKED";
        List<Map<String, Object>> events = jdbcTemplate.queryForList(selectSql);

        if (events.isEmpty()) return;

        for (Map<String, Object> event : events) {
            UUID eventId = (UUID) event.get("event_id");
            String aggregateId = (String) event.get("aggregate_id");
            String payload = (String) event.get("payload");

            // 2. We use executeInTransaction to bind the Kafka message to the DB transaction
            kafkaTemplate.executeInTransaction(kafkaOperations -> {
                // The key (aggregateId) guarantees partition ordering!
                kafkaOperations.send("product-events", aggregateId, payload);
                return true;
            });

            String deleteSql = "DELETE FROM outbox_events WHERE event_id = ?";
            jdbcTemplate.update(deleteSql, eventId);
            System.out.println("✅ Transactionally published and deleted event: " + eventId);
        }
    }
}