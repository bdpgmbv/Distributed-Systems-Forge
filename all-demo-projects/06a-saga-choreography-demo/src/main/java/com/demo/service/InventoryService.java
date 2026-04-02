package com.demo.service;
import com.demo.model.SagaEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j @Service @RequiredArgsConstructor
public class InventoryService {
    private final SagaEventBus bus;
    private int stock = 5;

    @EventListener @Transactional
    public void onPaymentCompleted(SagaEvent e) {
        if (!"PAYMENT_COMPLETED".equals(e.getEventType())) return;
        if (stock <= 0) {
            log.error("📦 Inventory FAILED (no stock) for order {}", e.getOrderId());
            bus.publish(e.getOrderId(), "INVENTORY_FAILED", "inventory-service", "out of stock");
        } else {
            stock--;
            log.info("📦 Reserved for order {} (remaining: {})", e.getOrderId(), stock);
            bus.publish(e.getOrderId(), "INVENTORY_RESERVED", "inventory-service", "ok");
        }
    }
}
