package com.demo.controller;

import com.demo.model.User;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * Circuit Breaker Demo.
 *
 * HOW TO TEST (follow this exact sequence):
 *
 * 1. Call the flaky endpoint 8 times rapidly:
 *    for i in {1..8}; do curl -s localhost:8080/demo/cb/flaky/1 | jq; done
 *    → Watch: first few calls alternate success/fail
 *    → After 50% failure rate reached → circuit OPENS
 *    → Subsequent calls return fallback INSTANTLY (no backend call!)
 *
 * 2. Check circuit breaker state:
 *    curl localhost:8080/actuator/circuitbreakers
 *
 * 3. Wait 10 seconds (wait-duration-in-open-state), then call again:
 *    curl localhost:8080/demo/cb/flaky/1
 *    → Circuit moves to HALF_OPEN, allows 2 test calls
 *
 * 4. Call the "always down" endpoint:
 *    for i in {1..6}; do curl -s localhost:8080/demo/cb/down/1 | jq; done
 *    → Circuit opens quickly → all subsequent calls get fallback
 */
@Slf4j
@RestController
@RequestMapping("/demo/cb")
public class DemoController {

    @Autowired private RestTemplate restTemplate;

    @org.springframework.context.annotation.Bean
    public RestTemplate restTemplate() { return new RestTemplate(); }

    /**
     * Calls the FLAKY endpoint (fails 50% of the time).
     * Circuit breaker monitors failure rate and opens when threshold hit.
     */
    @GetMapping("/flaky/{id}")
    @CircuitBreaker(name = "userService", fallbackMethod = "fallback")
    public Map<String, Object> callFlaky(@PathVariable Long id) {
        log.info("⚡ Calling flaky endpoint...");
        User user = restTemplate.getForObject(
            "http://localhost:8080/api/users/flaky/{id}", User.class, id);
        return Map.of("status", "SUCCESS", "user", user,
            "note", "Call went through to backend");
    }

    /**
     * Calls the ALWAYS-DOWN endpoint.
     * Circuit opens fast → all calls get fallback.
     */
    @GetMapping("/down/{id}")
    @CircuitBreaker(name = "userService", fallbackMethod = "fallback")
    public Map<String, Object> callDown(@PathVariable Long id) {
        log.info("💀 Calling always-down endpoint...");
        User user = restTemplate.getForObject(
            "http://localhost:8080/api/users/down/{id}", User.class, id);
        return Map.of("status", "SUCCESS", "user", user);
    }

    /**
     * Calls NORMAL endpoint (always succeeds).
     * Shows that circuit breaker is transparent for healthy calls.
     */
    @GetMapping("/normal/{id}")
    @CircuitBreaker(name = "userService", fallbackMethod = "fallback")
    public Map<String, Object> callNormal(@PathVariable Long id) {
        User user = restTemplate.getForObject(
            "http://localhost:8080/api/users/{id}", User.class, id);
        return Map.of("status", "SUCCESS", "user", user);
    }

    /**
     * FALLBACK — returned when circuit is OPEN or call fails.
     * Must match: same return type + extra Exception parameter.
     */
    private Map<String, Object> fallback(Long id, Exception ex) {
        log.warn("🔴 FALLBACK for user {} — reason: {}", id, ex.getMessage());
        return Map.of(
            "status", "FALLBACK",
            "user", new User(id, "Guest (fallback)", "N/A"),
            "reason", ex.getClass().getSimpleName() + ": " + ex.getMessage(),
            "note", "Circuit breaker returned fallback — no backend call made!"
        );
    }
}
