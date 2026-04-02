package com.demo.controller;
import com.demo.model.*;
import com.demo.service.OrderService;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.*;

@RestController @RequiredArgsConstructor
public class SagaController {
    private final OrderService orderService;
    private final EntityManager em;

    @PostMapping("/orders")
    public Map<String,Object> create(@RequestParam String customerId, @RequestParam BigDecimal amount) {
        Order order = orderService.createOrder(customerId, amount);
        em.refresh(order);
        return Map.of("orderId", order.getId(), "status", order.getStatus(),
            "failureReason", order.getFailureReason() != null ? order.getFailureReason() : "none");
    }

    @GetMapping("/orders")
    public List<Order> all() { return em.createQuery("FROM Order", Order.class).getResultList(); }

    @GetMapping("/events")
    public List<SagaEvent> events() { return em.createQuery("FROM SagaEvent ORDER BY id", SagaEvent.class).getResultList(); }
}
