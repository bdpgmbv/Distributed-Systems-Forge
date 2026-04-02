package com.demo.service;
import com.demo.model.SagaEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;

@Slf4j @Service @RequiredArgsConstructor
public class PaymentService {
    private final SagaEventBus bus;

    @EventListener @Transactional
    public void onOrderCreated(SagaEvent e) {
        if (!"ORDER_CREATED".equals(e.getEventType())) return;
        BigDecimal amount = new BigDecimal(e.getDetails().replace("amount=",""));
        if (amount.compareTo(new BigDecimal("500")) > 0) {
            log.error("💳 Payment FAILED for order {} (>{} limit)", e.getOrderId(), 500);
            bus.publish(e.getOrderId(), "PAYMENT_FAILED", "payment-service", "insufficient funds");
        } else {
            log.info("💳 Payment SUCCESS for order {}", e.getOrderId());
            bus.publish(e.getOrderId(), "PAYMENT_COMPLETED", "payment-service", "charged " + amount);
        }
    }

    @EventListener @Transactional
    public void onRefund(SagaEvent e) {
        if (!"REFUND_REQUESTED".equals(e.getEventType())) return;
        log.info("💳 REFUND processed for order {}", e.getOrderId());
    }
}
