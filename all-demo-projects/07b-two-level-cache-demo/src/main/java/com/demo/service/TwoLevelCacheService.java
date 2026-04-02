package com.demo.service;

import com.demo.model.User;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * TWO-LEVEL CACHE — simulates L1 (Caffeine) + L2 (simulated Redis) + DB.
 *
 * L1 (Caffeine): ~0.01ms, per-JVM, 100 entries max, 30s TTL
 * L2 (simulated Redis): ~1ms, shared across pods, 1000 entries, 5min TTL
 * DB: ~30ms (simulated with sleep)
 */
@Slf4j
@Service
public class TwoLevelCacheService {

    // L1: Local JVM cache (Caffeine) — blazing fast, small
    private final Cache<String, User> l1 = Caffeine.newBuilder()
        .maximumSize(100).expireAfterWrite(Duration.ofSeconds(30)).build();

    // L2: Simulating Redis (in production: RedisTemplate)
    private final Map<String, User> l2 = new ConcurrentHashMap<>();

    // Simulated DB
    private final Map<Long, User> db = new ConcurrentHashMap<>(Map.of(
        1L, new User(1L, "Alice", "alice@co.com"),
        2L, new User(2L, "Bob", "bob@co.com"),
        3L, new User(3L, "Charlie", "charlie@co.com")
    ));

    public record CacheResult(User user, String source, long elapsedMs) {}

    public CacheResult get(Long id) {
        String key = "user:" + id;
        long start = System.nanoTime();

        // L1 check
        User cached = l1.getIfPresent(key);
        if (cached != null) {
            long ms = (System.nanoTime() - start) / 1_000_000;
            log.info("⚡ L1 HIT (Caffeine) for user {} — {}ms", id, ms);
            return new CacheResult(cached, "L1_CAFFEINE", ms);
        }

        // L2 check
        cached = l2.get(key);
        if (cached != null) {
            l1.put(key, cached); // Promote to L1
            long ms = (System.nanoTime() - start) / 1_000_000;
            log.info("🟡 L2 HIT (Redis) for user {} — promoted to L1 — {}ms", id, ms);
            return new CacheResult(cached, "L2_REDIS", ms);
        }

        // DB (slow!)
        simulateDbLatency();
        User fromDb = db.get(id);
        if (fromDb == null) return new CacheResult(null, "DB_MISS", 0);

        l1.put(key, fromDb);
        l2.put(key, fromDb);
        long ms = (System.nanoTime() - start) / 1_000_000;
        log.info("🗄️ DB QUERY for user {} — stored in L1 + L2 — {}ms", id, ms);
        return new CacheResult(fromDb, "DATABASE", ms);
    }

    public void evict(Long id) {
        String key = "user:" + id;
        l1.invalidate(key);
        l2.remove(key);
        log.info("🗑️ Evicted user {} from L1 + L2", id);
    }

    public void evictL1Only(Long id) {
        l1.invalidate("user:" + id);
        log.info("🗑️ Evicted user {} from L1 only (L2 still has it)", id);
    }

    public Map<String, Object> stats() {
        return Map.of("l1_size", l1.estimatedSize(), "l2_size", l2.size(),
            "l1_stats", l1.stats().toString());
    }

    private void simulateDbLatency() { try { Thread.sleep(50); } catch (Exception e) {} }
}
