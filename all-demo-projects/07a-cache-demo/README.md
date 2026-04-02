# 07a — Caching Demo (@Cacheable with Caffeine)

No Docker needed! Uses Caffeine (in-memory) + H2 database.

## Run
```bash
./gradlew bootRun
```

## Test (watch the timing!)
```bash
# First call: CACHE MISS → hits DB (500ms simulated)
curl localhost:8080/products/1/timed

# Second call: CACHE HIT → from Caffeine (<1ms!)
curl localhost:8080/products/1/timed

# Update: @CachePut updates both DB and cache
curl -X PUT "localhost:8080/products/1?name=Gaming+Laptop&price=1299.99"
curl localhost:8080/products/1/timed    # still fast — cache updated!

# Evict: @CacheEvict removes from cache
curl -X DELETE localhost:8080/products/3
curl localhost:8080/products/2/timed    # still cached
curl -X POST localhost:8080/products/clear-cache
curl localhost:8080/products/2/timed    # MISS again — cache was cleared
```

## Key Annotations
- @Cacheable: check cache first, DB on miss, store in cache
- @CachePut: always execute, update cache with result
- @CacheEvict: remove from cache
- @CacheEvict(allEntries=true): clear entire cache
