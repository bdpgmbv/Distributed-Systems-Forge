package com.demo.controller;

import com.demo.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.Map;

/**
 * WebClient Demo — non-blocking reactive HTTP client.
 *
 * curl localhost:8080/demo/users/1           — basic GET
 * curl localhost:8080/demo/users             — GET all (Flux)
 * curl localhost:8080/demo/parallel          — 3 calls AT ONCE
 * curl localhost:8080/demo/timeout           — timeout after 3s
 * curl localhost:8080/demo/retry             — retries 3x with backoff
 */
@Slf4j
@RestController
@RequestMapping("/demo")
@RequiredArgsConstructor
public class DemoController {

    private final WebClient webClient;

    /** GET single — Mono (0 or 1 result, thread never blocks) */
    @GetMapping("/users/{id}")
    public Mono<User> getUser(@PathVariable Long id) {
        return webClient.get().uri("/api/users/{id}", id)
            .retrieve().bodyToMono(User.class);
    }

    /** GET all — Flux (0..N results, streamed one by one) */
    @GetMapping("/users")
    public Flux<User> getAllUsers() {
        return webClient.get().uri("/api/users")
            .retrieve().bodyToFlux(User.class);
    }

    /** POST */
    @PostMapping("/users")
    public Mono<User> createUser(@RequestBody User user) {
        return webClient.post().uri("/api/users").bodyValue(user)
            .retrieve().bodyToMono(User.class);
    }

    /**
     * PARALLEL — THE WebClient superpower.
     * Mono.zip fires ALL calls simultaneously.
     * 3 calls × 100ms each = 100ms total (not 300ms).
     * curl localhost:8080/demo/parallel
     */
    @GetMapping("/parallel")
    public Mono<Map<String, Object>> parallel() {
        long start = System.currentTimeMillis();
        Mono<User> u1 = webClient.get().uri("/api/users/1").retrieve().bodyToMono(User.class);
        Mono<User> u2 = webClient.get().uri("/api/users/2").retrieve().bodyToMono(User.class);
        Mono<User> u3 = webClient.get().uri("/api/users/3").retrieve().bodyToMono(User.class);

        return Mono.zip(u1, u2, u3).map(t -> {
            long ms = System.currentTimeMillis() - start;
            return Map.<String, Object>of(
                "user1", t.getT1(), "user2", t.getT2(), "user3", t.getT3(),
                "total_ms", ms,
                "lesson", "All 3 ran in PARALLEL. With RestTemplate: 3x slower.");
        });
    }

    /**
     * TIMEOUT — calls 10s slow endpoint with 3s timeout.
     * curl localhost:8080/demo/timeout
     */
    @GetMapping("/timeout")
    public Mono<Map<String, Object>> timeout() {
        long start = System.currentTimeMillis();
        return webClient.get().uri("/api/users/slow/1?delayMs=10000")
            .retrieve().bodyToMono(User.class)
            .timeout(Duration.ofSeconds(3))
            .map(u -> Map.<String, Object>of("status", "SUCCESS", "user", u))
            .onErrorResume(e -> Mono.just(Map.of(
                "status", "TIMEOUT", "elapsed_ms", System.currentTimeMillis() - start,
                "lesson", "Timed out after 3s. Thread was NOT blocked during wait!")));
    }

    /**
     * RETRY — retries 3x with exponential backoff on failure.
     * curl localhost:8080/demo/retry
     */
    @GetMapping("/retry")
    public Mono<Map<String, Object>> retry() {
        return webClient.get().uri("/api/users/999").retrieve()
            .bodyToMono(User.class)
            .retryWhen(Retry.backoff(3, Duration.ofMillis(500))
                .doBeforeRetry(s -> log.info("🔄 Retry #{}", s.totalRetries() + 1)))
            .map(u -> Map.<String, Object>of("user", u))
            .onErrorResume(e -> Mono.just(Map.of(
                "status", "ALL_RETRIES_FAILED", "attempts", 4,
                "lesson", "Retried 3x with 500ms/1s/2s backoff. All failed (user 999 doesn't exist).")));
    }
}
