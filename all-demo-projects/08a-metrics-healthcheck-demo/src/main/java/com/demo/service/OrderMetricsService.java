package com.demo.service;
import io.micrometer.core.instrument.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j @Service
public class OrderMetricsService {
    private final Counter orderCounter;
    private final Timer orderTimer;
    private final AtomicInteger active = new AtomicInteger(0);

    public OrderMetricsService(MeterRegistry r) {
        orderCounter = Counter.builder("orders.created.total").description("Total orders").register(r);
        orderTimer = Timer.builder("orders.processing.duration").publishPercentiles(0.5,0.95,0.99).register(r);
        Gauge.builder("orders.active", active, AtomicInteger::get).register(r);
    }

    public String processOrder(String cid) {
        return orderTimer.record(() -> {
            active.incrementAndGet();
            try { Thread.sleep((long)(Math.random()*500)); orderCounter.increment(); return "ORD-"+System.currentTimeMillis(); }
            catch (Exception e) { throw new RuntimeException(e); }
            finally { active.decrementAndGet(); }
        });
    }
}