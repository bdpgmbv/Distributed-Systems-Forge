package com.vyshaliprabananthlal.payment.controller;

/**
 * 3/2/26 - 17:01
 *
 * @author Vyshali Prabananth Lal
 */

import com.vyshaliprabananthlal.common.FraudRequest;
import com.vyshaliprabananthlal.common.FraudResponse;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.requestreply.RequestReplyFuture;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final ReplyingKafkaTemplate<String, FraudRequest, FraudResponse> kafkaTemplate;

    @Value("${app.kafka.topic.fraud-request}")
    private String requestTopic;

    @Value("${app.kafka.topic.fraud-reply}")
    private String replyTopic;

    public PaymentController(ReplyingKafkaTemplate<String, FraudRequest, FraudResponse> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @PostMapping("/process")
    public ResponseEntity<String> processPayment(@RequestParam String accountId, @RequestParam Double amount) {

        // 1. Create our DTO
        String transactionId = UUID.randomUUID().toString();
        FraudRequest requestDto = new FraudRequest(transactionId, accountId, amount);

        // 2. Wrap it in a Kafka ProducerRecord
        ProducerRecord<String, FraudRequest> record = new ProducerRecord<>(requestTopic, transactionId, requestDto);

        try {
            // 3. Send the message and get a Future back.
            // Spring automatically injects the Correlation ID into the Kafka headers here!
            RequestReplyFuture<String, FraudRequest, FraudResponse> replyFuture = kafkaTemplate.sendAndReceive(record);

            // 4. .get() blocks the HTTP thread until the reply comes back or the 10-second timeout hits
            ConsumerRecord<String, FraudResponse> consumerRecord = replyFuture.get();
            FraudResponse responseDto = consumerRecord.value();

            // 5. Evaluate the response
            if (responseDto.isFraudulent()) {
                return ResponseEntity.status(403).body("Payment Rejected: " + responseDto.reason());
            } else {
                return ResponseEntity.ok("Payment Approved for Transaction ID: " + transactionId);
            }

        } catch (ExecutionException | InterruptedException e) {
            // This will trigger if the 10-second timeout expires!
            return ResponseEntity.status(504).body("Fraud check timed out. Please try again later.");
        }
    }
}
