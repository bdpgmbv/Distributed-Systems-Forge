package com.demo.controller;

import com.demo.model.User;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.Map;

/**
 * Retry + Rate Limiter Demo.
 *
 * RETRY TEST:
 *   curl localhost:8080/demo/retry/flaky/1
 *   → Calls flaky endpoint. Fails? Retries 3 more times with exponential backoff.
 *   → Watch logs: Retry #1 (500ms), #2 (1000ms), #3 (2000ms)
 *
 * RATE LIMITER TEST:
 *   for i in {1..10}; do curl -s localhost:8080/demo/ratelimit; done
 *   → First 5 succeed (limit=5 per 10s). Next 5 get 429 Too Many Requests.
 */
@Slf4j @RestController @RequestMapping("/demo")
public class DemoController {

    @Autowired private RestTemplate restTemplate;
    @org.springframework.context.annotation.Bean
    public RestTemplate rt() { return new RestTemplate(); }

    @GetMapping("/retry/flaky/{id}")
    @Retry(name = "userService", fallbackMethod = "retryFallback")
    public Map<String, Object> retryFlaky(@PathVariable Long id) {
        log.info("🔄 Attempting call to flaky endpoint...");
        User user = restTemplate.getForObject(
            "http://localhost:8080/api/users/flaky/{id}", User.class, id);
        return Map.of("status", "SUCCESS", "user", user, "time", Instant.now().toString());
    }

    private Map<String, Object> retryFallback(Long id, Exception ex) {
        log.error("💀 ALL RETRIES EXHAUSTED for user {}", id);
        return Map.of("status", "ALL_RETRIES_FAILED", "attempts", 4,
            "error", ex.getMessage(),
            "note", "Tried 4 times with 500ms→1000ms→2000ms backoff. All failed.");
    }

    @GetMapping("/retry/down/{id}")
    @Retry(name = "userService", fallbackMethod = "retryFallback")
    public Map<String, Object> retryDown(@PathVariable Long id) {
        log.info("🔄 Calling always-down endpoint (will retry 3 times)...");
        User user = restTemplate.getForObject(
            "http://localhost:8080/api/users/down/{id}", User.class, id);
        return Map.of("status", "SUCCESS", "user", user);
    }

    @GetMapping("/ratelimit")
    @RateLimiter(name = "apiLimit", fallbackMethod = "rateLimitFallback")
    public Map<String, Object> rateLimited() {
        log.info("✅ Request within rate limit");
        return Map.of("status", "OK", "time", Instant.now().toString(),
            "note", "You have 5 requests per 10 seconds");
    }

    private Map<String, Object> rateLimitFallback(Exception ex) {
        log.warn("🚫 RATE LIMITED!");
        throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS,
            "Rate limit exceeded. 5 requests per 10 seconds. Wait and try again.");
    }
}
