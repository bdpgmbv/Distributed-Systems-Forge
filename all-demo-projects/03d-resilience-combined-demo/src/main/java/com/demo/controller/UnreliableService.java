package com.demo.controller;
import com.demo.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * SIMULATES an unreliable remote service.
 * /api/users/{id}         — normal endpoint
 * /api/users/flaky/{id}   — fails every other call
 * /api/users/slow/{id}    — responds after delay
 * /api/users/down/{id}    — always fails (500)
 */
@Slf4j @RestController @RequestMapping("/api/users")
public class UnreliableService {
    private final AtomicInteger counter = new AtomicInteger(0);

    @GetMapping("/{id}")
    public User get(@PathVariable Long id) {
        log.info("✅ /api/users/{} — normal response", id);
        return new User(id, "Alice", "alice@co.com");
    }

    @GetMapping("/flaky/{id}")
    public User flaky(@PathVariable Long id) {
        int c = counter.incrementAndGet();
        if (c % 2 == 0) {
            log.info("✅ /api/users/flaky/{} — success (call #{})", id, c);
            return new User(id, "Alice", "alice@co.com");
        }
        log.error("❌ /api/users/flaky/{} — FAILURE (call #{})", id, c);
        throw new RuntimeException("Simulated failure #" + c);
    }

    @GetMapping("/slow/{id}")
    public User slow(@PathVariable Long id, @RequestParam(defaultValue="5000") long delayMs) throws Exception {
        log.info("🐌 /api/users/slow/{} — sleeping {}ms", id, delayMs);
        Thread.sleep(delayMs);
        return new User(id, "Alice", "alice@co.com");
    }

    @GetMapping("/down/{id}")
    public User down(@PathVariable Long id) {
        log.error("💀 /api/users/down/{} — always fails", id);
        throw new RuntimeException("Service is down!");
    }

    @GetMapping("/status")
    public Map<String,Object> status() {
        return Map.of("totalCalls", counter.get());
    }
}
