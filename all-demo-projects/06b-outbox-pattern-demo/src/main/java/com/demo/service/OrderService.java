package com.demo.service;
import com.demo.model.*;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.Instant;
@Slf4j @Service @RequiredArgsConstructor
public class OrderService {
    private final EntityManager em;
    @Transactional
    public Order createOrder(String customerId, BigDecimal amount) {
        Order order = Order.builder().customerId(customerId).amount(amount).status("CREATED").build();
        em.persist(order); em.flush();
        OutboxEvent event = OutboxEvent.builder().aggregateType("Order").aggregateId(String.valueOf(order.getId()))
            .eventType("OrderCreated").payload("{\"orderId\":"+order.getId()+",\"amount\":"+amount+"}").published(false).createdAt(Instant.now()).build();
        em.persist(event);
        log.info("✅ Order {} + outbox event saved ATOMICALLY", order.getId());
        return order;
    }
}
