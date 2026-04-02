package com.demo.controller;

import com.demo.model.User;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Bulkhead + Timeout Demo.
 *
 * BULKHEAD TEST (max 3 concurrent):
 *   # Send 6 requests simultaneously (3 will be rejected!)
 *   for i in {1..6}; do curl -s localhost:8080/demo/bulkhead/slow/1 & done; wait
 *   → 3 succeed, 3 get "Bulkhead full" fallback
 *
 * TIMEOUT TEST (2s limit):
 *   curl localhost:8080/demo/timeout/1?delayMs=1000   # succeeds (1s < 2s)
 *   curl localhost:8080/demo/timeout/1?delayMs=5000   # times out (5s > 2s)
 */
@Slf4j @RestController @RequestMapping("/demo")
public class DemoController {

    @Autowired private RestTemplate restTemplate;
    @org.springframework.context.annotation.Bean
    public RestTemplate rt() { return new RestTemplate(); }

    @GetMapping("/bulkhead/slow/{id}")
    @Bulkhead(name = "userService", fallbackMethod = "bulkheadFallback")
    public Map<String, Object> bulkheadCall(@PathVariable Long id) {
        log.info("🚢 Bulkhead: request entered (calling slow endpoint 3s)...");
        User user = restTemplate.getForObject(
            "http://localhost:8080/api/users/slow/{id}?delayMs=3000", User.class, id);
        return Map.of("status", "SUCCESS", "user", user);
    }

    private Map<String, Object> bulkheadFallback(Long id, Exception ex) {
        log.warn("🚫 BULKHEAD FULL — request rejected: {}", ex.getMessage());
        return Map.of("status", "BULKHEAD_FULL",
            "note", "Max 3 concurrent calls. This request was rejected because all 3 slots are busy.",
            "reason", ex.getClass().getSimpleName());
    }

    @GetMapping("/timeout/{id}")
    @TimeLimiter(name = "userService", fallbackMethod = "timeoutFallback")
    public CompletableFuture<Map<String, Object>> timeoutCall(
            @PathVariable Long id, @RequestParam(defaultValue = "1000") long delayMs) {
        return CompletableFuture.supplyAsync(() -> {
            log.info("⏱️ Timeout: calling slow endpoint ({}ms delay, 2s limit)...", delayMs);
            User user = restTemplate.getForObject(
                "http://localhost:8080/api/users/slow/{id}?delayMs={delay}", User.class, id, delayMs);
            return Map.<String, Object>of("status", "SUCCESS", "user", user,
                "elapsed_ms", delayMs, "note", "Completed within 2s timeout");
        });
    }

    private CompletableFuture<Map<String, Object>> timeoutFallback(Long id, long delayMs, Exception ex) {
        log.warn("⏱️ TIMEOUT after 2s for user {}", id);
        return CompletableFuture.completedFuture(Map.of(
            "status", "TIMEOUT",
            "note", "TimeLimiter cancelled after 2 seconds. The slow call (" + delayMs + "ms) was too slow.",
            "lesson", "Thread was freed! Not blocked for " + delayMs + "ms."));
    }
}
