package com.vyshaliprabananthlal.fraud.listener;

/**
 * 3/2/26 - 17:16
 *
 * @author Vyshali Prabananth Lal
 */

import com.vyshaliprabananthlal.common.FraudRequest;
import com.vyshaliprabananthlal.common.FraudResponse;
import com.vyshaliprabananthlal.fraud.repository.FraudRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Component;

@Component
public class FraudListener {

    private final FraudRepository fraudRepository;

    public FraudListener(FraudRepository fraudRepository) {
        this.fraudRepository = fraudRepository;
    }

    @KafkaListener(topics = "${app.kafka.topic.fraud-request}", groupId = "fraud-checker-group")
    @SendTo // <-- MAGIC: Replies to the topic specified in the request's Kafka headers
    public FraudResponse handleFraudCheck(FraudRequest request) {
        System.out.println("-> Received Fraud Check for Account: " + request.accountId());

        // 1. Check our PostgreSQL Database using plain JDBC
        String blockReason = fraudRepository.checkAccountStatus(request.accountId());

        // 2. Build the response based on the DB result
        if (blockReason != null) {
            System.out.println("<- Fraud DETECTED. Rejecting.");
            return new FraudResponse(request.transactionId(), true, blockReason);
        }

        System.out.println("<- Account is clean. Approving.");
        return new FraudResponse(request.transactionId(), false, "All good");
    }
}
