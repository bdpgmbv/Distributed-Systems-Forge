package com.demo.service;
import com.demo.model.*;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;

@Slf4j @Service @RequiredArgsConstructor
public class OrderService {
    private final EntityManager em;
    private final SagaEventBus bus;

    @Transactional
    public Order createOrder(String customerId, BigDecimal amount) {
        Order order = Order.builder().customerId(customerId).amount(amount).status(OrderStatus.PENDING).build();
        em.persist(order); em.flush();
        bus.publish(order.getId(), "ORDER_CREATED", "order-service", "amount=" + amount);
        return order;
    }

    @EventListener @Transactional
    public void onPaymentFailed(SagaEvent e) {
        if (!"PAYMENT_FAILED".equals(e.getEventType())) return;
        Order o = em.find(Order.class, e.getOrderId());
        o.setStatus(OrderStatus.CANCELLED); o.setFailureReason("Payment: " + e.getDetails());
        log.info("🔙 COMPENSATION: Order {} CANCELLED", o.getId());
    }

    @EventListener @Transactional
    public void onInventoryFailed(SagaEvent e) {
        if (!"INVENTORY_FAILED".equals(e.getEventType())) return;
        Order o = em.find(Order.class, e.getOrderId());
        o.setStatus(OrderStatus.CANCELLED); o.setFailureReason("Inventory: " + e.getDetails());
        bus.publish(o.getId(), "REFUND_REQUESTED", "order-service", "trigger refund");
        log.info("🔙 COMPENSATION: Order {} CANCELLED + refund triggered", o.getId());
    }

    @EventListener @Transactional
    public void onInventoryReserved(SagaEvent e) {
        if (!"INVENTORY_RESERVED".equals(e.getEventType())) return;
        Order o = em.find(Order.class, e.getOrderId());
        o.setStatus(OrderStatus.CONFIRMED);
        log.info("✅ Order {} CONFIRMED!", o.getId());
    }
}
