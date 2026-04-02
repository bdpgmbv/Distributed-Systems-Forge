package com.demo.controller;

import com.demo.model.User;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * ALL 4 RESILIENCE PATTERNS COMBINED on one endpoint.
 *
 * Execution order (configured by aspect-order in yml):
 *   RateLimiter(4) → Retry(2) → CircuitBreaker(1) → Bulkhead(3) → actual call
 *
 * WHAT HAPPENS:
 * 1. RateLimiter: "Are we within 10 req/10s?" If no → 429
 * 2. Retry: "If call fails, retry 2 more times with backoff"
 * 3. CircuitBreaker: "Is circuit OPEN?" If yes → fallback immediately
 * 4. Bulkhead: "Is there a free slot (max 5)?" If no → reject
 * 5. Actual HTTP call
 *
 * TEST:
 *   # Normal call
 *   curl localhost:8080/demo/combined/1
 *
 *   # Flaky (retries + circuit breaker)
 *   for i in {1..20}; do curl -s localhost:8080/demo/combined/flaky/1 | python3 -c "import sys,json;d=json.load(sys.stdin);print(d.get('status','?'))"; done
 *
 *   # Rate limit test (10 per 10s)
 *   for i in {1..15}; do curl -s -w "%{http_code}\n" -o /dev/null localhost:8080/demo/combined/1; done
 *
 *   # Check circuit breaker state
 *   curl localhost:8080/actuator/circuitbreakers
 */
@Slf4j @RestController @RequestMapping("/demo/combined")
public class DemoController {

    @Autowired private RestTemplate restTemplate;
    @org.springframework.context.annotation.Bean
    public RestTemplate rt() { return new RestTemplate(); }

    // ALL 4 patterns stacked!
    @GetMapping("/{id}")
    @RateLimiter(name = "userService")
    @Retry(name = "userService")
    @CircuitBreaker(name = "userService", fallbackMethod = "fallback")
    @Bulkhead(name = "userService")
    public Map<String, Object> combinedCall(@PathVariable Long id) {
        log.info("🎯 Combined call — passed all 4 resilience checks!");
        User user = restTemplate.getForObject(
            "http://localhost:8080/api/users/{id}", User.class, id);
        return Map.of("status", "SUCCESS", "user", user,
            "patterns", "RateLimiter ✅ → Retry ✅ → CircuitBreaker ✅ → Bulkhead ✅ → Call ✅");
    }

    @GetMapping("/flaky/{id}")
    @RateLimiter(name = "userService")
    @Retry(name = "userService")
    @CircuitBreaker(name = "userService", fallbackMethod = "fallback")
    @Bulkhead(name = "userService")
    public Map<String, Object> combinedFlaky(@PathVariable Long id) {
        log.info("🎯 Combined flaky call...");
        User user = restTemplate.getForObject(
            "http://localhost:8080/api/users/flaky/{id}", User.class, id);
        return Map.of("status", "SUCCESS", "user", user);
    }

    private Map<String, Object> fallback(Long id, Exception ex) {
        log.warn("🔴 COMBINED FALLBACK: {}", ex.getMessage());
        return Map.of("status", "FALLBACK", "reason", ex.getClass().getSimpleName(),
            "user", new User(id, "Fallback User", "N/A"),
            "note", "One of the 4 patterns triggered this fallback");
    }
}
