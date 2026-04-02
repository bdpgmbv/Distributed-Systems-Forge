package com.demo.controller;
import com.demo.config.CustomHealthIndicator;
import com.demo.service.OrderMetricsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController @RequiredArgsConstructor
public class DemoController {
    private final OrderMetricsService metrics;
    private final CustomHealthIndicator health;

    @GetMapping("/orders/create")
    public Map<String,Object> create(@RequestParam String customerId) {
        return Map.of("orderId", metrics.processOrder(customerId), "note", "Check /actuator/prometheus");
    }

    @PostMapping("/health/toggle")
    public Map<String,Object> toggle() {
        boolean now = !health.healthy.get(); health.healthy.set(now);
        return Map.of("healthy", now);
    }
}