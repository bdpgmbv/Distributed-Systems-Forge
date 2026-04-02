package com.demo.controller;
import com.demo.model.*;
import com.demo.service.OrderService;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.*;
@RestController @RequiredArgsConstructor
public class OutboxController {
    private final OrderService orderService;
    private final EntityManager em;
    @PostMapping("/orders")
    public Map<String,Object> create(@RequestParam String customerId, @RequestParam BigDecimal amount) {
        Order o = orderService.createOrder(customerId, amount);
        return Map.of("orderId",o.getId(),"note","Order + outbox event saved atomically. Poller publishes within 2s.");
    }
    @GetMapping("/orders") public List<Order> orders() { return em.createQuery("FROM Order",Order.class).getResultList(); }
    @GetMapping("/outbox") public List<OutboxEvent> outbox() { return em.createQuery("FROM OutboxEvent ORDER BY id",OutboxEvent.class).getResultList(); }
}
