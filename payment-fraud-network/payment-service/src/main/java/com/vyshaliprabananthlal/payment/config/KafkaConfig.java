package com.vyshaliprabananthlal.payment.config;

/**
 * 3/2/26 - 16:59
 *
 * @author Vyshali Prabananth Lal
 */

import com.vyshaliprabananthlal.common.FraudRequest;
import com.vyshaliprabananthlal.common.FraudResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;

@Configuration
public class KafkaConfig {

    @Value("${app.kafka.topic.fraud-reply}")
    private String replyTopic;

    // 1. We create a listener container that specifically watches our reply topic
    @Bean
    public ConcurrentMessageListenerContainer<String, FraudResponse> replyContainer(ConcurrentKafkaListenerContainerFactory<String, FraudResponse> containerFactory) {

        ConcurrentMessageListenerContainer<String, FraudResponse> container = containerFactory.createContainer(replyTopic);
        container.getContainerProperties().setGroupId("payment-reply-group");
        return container;
    }

    // 2. We inject that container into our ReplyingKafkaTemplate
    @Bean
    public ReplyingKafkaTemplate<String, FraudRequest, FraudResponse> replyingTemplate(ProducerFactory<String, FraudRequest> producerFactory, ConcurrentMessageListenerContainer<String, FraudResponse> replyContainer) {

        ReplyingKafkaTemplate<String, FraudRequest, FraudResponse> template = new ReplyingKafkaTemplate<>(producerFactory, replyContainer);

        // If the Fraud service takes longer than 10 seconds, we fail fast.
        template.setDefaultReplyTimeout(java.time.Duration.ofSeconds(10));
        return template;
    }
}
