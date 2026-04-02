package com.demo.service;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j @Component
public class ChaosMonkey {
    public final AtomicBoolean enabled = new AtomicBoolean(true);
    @Value("${chaos.latency.min-ms:500}") private int minLatency;
    @Value("${chaos.latency.max-ms:3000}") private int maxLatency;
    @Value("${chaos.exception.rate:30}") private int exceptionRate;

    public void maybeAttack(String method) {
        if (!enabled.get()) return;
        // Random latency
        int delay = ThreadLocalRandom.current().nextInt(minLatency, maxLatency);
        log.info("🐒 Chaos: injecting {}ms latency into {}", delay, method);
        try { Thread.sleep(delay); } catch (Exception e) {}
        // Random exception
        if (ThreadLocalRandom.current().nextInt(100) < exceptionRate) {
            log.error("🐒 Chaos: injecting EXCEPTION into {}", method);
            throw new RuntimeException("Chaos Monkey struck! Service failure in " + method);
        }
    }
}