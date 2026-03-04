package com.vyshaliprabananthlal.ecommerce.payment.processor;

/**
 * 3/1/26 - 21:14
 *
 * @author Vyshali Prabananth Lal
 */

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.vyshaliprabananthlal.ecommerce.payment.repository.PaymentRepository;
import com.vyshaliprabananthlal.ecommerce.schema.OrderEvent; // The Avro class
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class PaymentProcessor {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final PaymentRepository paymentRepository;
    private final ObjectMapper objectMapper;

    public PaymentProcessor(KafkaTemplate<String, String> kafkaTemplate, PaymentRepository paymentRepository, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.paymentRepository = paymentRepository;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "payment-commands", groupId = "payment-group")
    public void processPayment(ConsumerRecord<String, OrderEvent> record) {
        try {
            // 1. Get the strongly typed Avro object from the record
            OrderEvent command = record.value();

            // We use native getter methods now! No more parsing JSON strings.
            String orderId = command.getOrderId().toString();
            double amount = command.getAmount();

            // 2. IDEMPOTENCY CHECK
            if (!paymentRepository.checkAndSaveIdempotencyKey(orderId)) {
                System.out.println("⚠️ Duplicate command detected for Order " + orderId + ". Safely ignoring!");
                return;
            }

            System.out.println("💳 Processing Avro Payment for Order: " + orderId + " for $" + amount);

            // 3. Simulate Business Logic
            boolean isSuccessful = Math.random() > 0.2; // 80% success rate
            ObjectNode reply = objectMapper.createObjectNode();
            reply.put("orderId", orderId);

            if (isSuccessful) {
                reply.put("status", "PAYMENT_SUCCESS");
                kafkaTemplate.send("payment-events", orderId, reply.toString());
                System.out.println("✅ Payment Succeeded!");
            } else {
                reply.put("status", "PAYMENT_FAILED");
                kafkaTemplate.send("payment-events", orderId, reply.toString());
                System.err.println("❌ Payment Failed! Insufficient funds.");
            }

        } catch (Exception e) {
            System.err.println("Failed to process payment command.");
            e.printStackTrace();
        }
    }
}