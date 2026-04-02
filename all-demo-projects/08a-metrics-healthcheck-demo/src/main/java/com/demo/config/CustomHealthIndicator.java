package com.demo.config;
import org.springframework.boot.actuate.health.*;
import org.springframework.stereotype.Component;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
public class CustomHealthIndicator implements HealthIndicator {
    public final AtomicBoolean healthy = new AtomicBoolean(true);
    public Health health() {
        return healthy.get() ? Health.up().withDetail("externalService","reachable").build()
            : Health.down().withDetail("externalService","unreachable").build();
    }
}