# 07b — Two-Level Cache Demo (L1 Caffeine + L2 simulated Redis)

No Docker! Simulates the two-level pattern in-memory.

## Run
```bash
./gradlew bootRun
```

## Test (watch the source and timing!)
```bash
# 1st call: DATABASE (50ms)
curl localhost:8080/users/1

# 2nd call: L1_CAFFEINE (<1ms!) — 50x faster!
curl localhost:8080/users/1

# Evict L1 only → next call goes to L2
curl -X POST localhost:8080/users/1/evict-l1
curl localhost:8080/users/1    # L2_REDIS → promoted back to L1

# Evict both → next call goes to DB
curl -X POST localhost:8080/users/1/evict
curl localhost:8080/users/1    # DATABASE again

# Cache stats
curl localhost:8080/cache/stats
```

## Architecture
```
Request → L1 (Caffeine, <1ms, 100 items, 30s TTL)
  miss → L2 (Redis sim, ~1ms, 1000 items, 5min TTL) → promote to L1
    miss → Database (~50ms) → store in L1 + L2
```
