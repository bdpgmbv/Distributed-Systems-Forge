package com.vyshaliprabananthlal.ecommerce.notification.service;

/**
 * 3/1/26 - 21:53
 *
 * @author Vyshali Prabananth Lal
 */

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class NotificationManager {

    private final JdbcTemplate jdbcTemplate;
    private final ObjectMapper objectMapper;

    public NotificationManager(JdbcTemplate jdbcTemplate, ObjectMapper objectMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.objectMapper = objectMapper;
    }

    /**
     * PATTERN: Fan-Out.
     * We listen to the exact same topic the Order Saga listens to.
     */
    @KafkaListener(topics = "payment-events", groupId = "notification-group")
    public void handlePaymentEvent(String payload) {
        try {
            ObjectNode event = (ObjectNode) objectMapper.readTree(payload);
            String orderId = event.get("orderId").asText();
            String status = event.get("status").asText();

            if ("PAYMENT_SUCCESS".equals(status)) {
                // 1. Instant Action
                System.out.println("⚡ FAN-OUT: Sending instant 'Order Confirmed' email for Order " + orderId);

                // 2. Delayed Action: Schedule a "Review your purchase" email for 1 minute from now
                // (In reality, this would be 3 days, but 1 minute is better for testing!)
                LocalDateTime sendTime = LocalDateTime.now().plusMinutes(1);
                String sql = "INSERT INTO delayed_emails (order_id, email_type, send_at) VALUES (?, ?, ?)";
                jdbcTemplate.update(sql, orderId, "REVIEW_REQUEST", sendTime);

                System.out.println("🕒 Scheduled delayed review email for Order " + orderId + " at " + sendTime);
            }
        } catch (Exception e) {
            System.err.println("Failed to process notification event: " + payload);
        }
    }

    /**
     * PATTERN: Delayed / Scheduled Message Processing.
     * This wakes up every 10 seconds, looks for emails whose "send_at" time has passed,
     * sends them, and marks them as COMPLETED.
     */
    @Scheduled(fixedDelay = 10000)
    @Transactional
    public void processDelayedEmails() {
        // We use our trusty SKIP LOCKED pattern so multiple instances don't send the same email
        String selectSql = """
                SELECT id, order_id, email_type 
                FROM delayed_emails 
                WHERE status = 'PENDING' AND send_at <= CURRENT_TIMESTAMP 
                FOR UPDATE SKIP LOCKED
                """;

        List<Map<String, Object>> emailsToSend = jdbcTemplate.queryForList(selectSql);

        if (emailsToSend.isEmpty()) {
            return; // Nothing to send yet
        }

        for (Map<String, Object> row : emailsToSend) {
            Integer id = (Integer) row.get("id");
            String orderId = (String) row.get("order_id");
            String type = (String) row.get("email_type");

            // Simulate sending the email
            System.out.println("📤 SENDING DELAYED EMAIL [" + type + "] for Order " + orderId);

            // Mark as completed so we don't send it again
            String updateSql = "UPDATE delayed_emails SET status = 'COMPLETED' WHERE id = ?";
            jdbcTemplate.update(updateSql, id);
        }
    }
}
