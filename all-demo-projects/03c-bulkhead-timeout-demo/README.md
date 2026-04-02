# 03c — Bulkhead + Timeout Demo

## Run
```bash
./gradlew bootRun
```

## Test Bulkhead (max 3 concurrent calls)
```bash
# Send 6 simultaneous requests to slow endpoint (takes 3s each)
for i in {1..6}; do curl -s localhost:8080/demo/bulkhead/slow/1 & done; wait

# Result: 3 succeed (inside bulkhead), 3 rejected (BULKHEAD_FULL)
```

## Test Timeout (2 second limit)
```bash
# Fast call (1s) — succeeds
curl "localhost:8080/demo/timeout/1?delayMs=1000"

# Slow call (5s) — times out after 2s
curl "localhost:8080/demo/timeout/1?delayMs=5000"
```

## Config
- Bulkhead: max 3 concurrent calls, 500ms wait for a slot
- TimeLimiter: 2s timeout, cancels the running future
