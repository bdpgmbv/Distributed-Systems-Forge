package com.demo.service;
import com.demo.model.SagaEvent;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;

@Slf4j @Service @RequiredArgsConstructor
public class SagaEventBus {
    private final ApplicationEventPublisher publisher;
    private final EntityManager em;

    @Transactional
    public void publish(Long orderId, String eventType, String service, String details) {
        SagaEvent event = SagaEvent.builder().orderId(orderId).eventType(eventType)
            .serviceName(service).details(details).timestamp(Instant.now()).build();
        em.persist(event);
        log.info("📨 EVENT: {} from {} for order {}", eventType, service, orderId);
        publisher.publishEvent(event);
    }
}
