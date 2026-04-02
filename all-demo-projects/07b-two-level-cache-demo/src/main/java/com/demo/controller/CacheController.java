package com.demo.controller;
import com.demo.service.TwoLevelCacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.*;

/**
 * curl localhost:8080/users/1        — 1st: DB (50ms). 2nd: L1 (<1ms!)
 * curl localhost:8080/users/1/evict-l1 — evict L1 only → next call hits L2
 * curl localhost:8080/users/1        — L2 HIT → promotes to L1
 * curl localhost:8080/users/1/evict  — evict both → next call hits DB
 * curl localhost:8080/cache/stats
 */
@RestController @RequiredArgsConstructor
public class CacheController {
    private final TwoLevelCacheService cache;

    @GetMapping("/users/{id}")
    public Map<String,Object> get(@PathVariable Long id) {
        var r = cache.get(id);
        return Map.of("user", r.user(), "source", r.source(), "elapsed_ms", r.elapsedMs(),
            "explanation", switch(r.source()) {
                case "L1_CAFFEINE" -> "Local JVM cache. <1ms. Fastest!";
                case "L2_REDIS" -> "Remote cache (simulated Redis). ~1ms. Promoted to L1.";
                case "DATABASE" -> "Full DB query. ~50ms. Stored in L1 + L2 for next time.";
                default -> "Not found";
            });
    }

    @PostMapping("/users/{id}/evict-l1")
    public Map<String,String> evictL1(@PathVariable Long id) {
        cache.evictL1Only(id);
        return Map.of("status", "L1 evicted. Next call hits L2 → promotes back to L1.");
    }

    @PostMapping("/users/{id}/evict")
    public Map<String,String> evict(@PathVariable Long id) {
        cache.evict(id);
        return Map.of("status", "L1 + L2 evicted. Next call hits DB.");
    }

    @GetMapping("/cache/stats")
    public Map<String,Object> stats() { return cache.stats(); }
}
