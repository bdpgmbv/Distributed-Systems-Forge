# 03a — Circuit Breaker Demo (Resilience4j)

## Run
```bash
./gradlew bootRun
```

## Test (follow this sequence!)

```bash
# Step 1: Call flaky endpoint 8 times rapidly
for i in {1..8}; do echo "--- Call $i ---"; curl -s localhost:8080/demo/cb/flaky/1 | python3 -m json.tool; done

# Watch the transition: SUCCESS → FAILURE → SUCCESS → FAILURE → FALLBACK (circuit OPEN!)

# Step 2: Check circuit state
curl -s localhost:8080/actuator/circuitbreakers | python3 -m json.tool

# Step 3: Wait 10 seconds, then try again (HALF_OPEN)
sleep 10 && curl -s localhost:8080/demo/cb/flaky/1 | python3 -m json.tool

# Step 4: Call always-down endpoint
for i in {1..6}; do curl -s localhost:8080/demo/cb/down/1 | python3 -m json.tool; done

# Step 5: Normal calls (circuit breaker is transparent)
curl localhost:8080/demo/cb/normal/1
```

## Config (application.yml)
- sliding-window-size: 6 (last 6 calls)
- failure-rate-threshold: 50% (open at 50% failure)
- wait-duration-in-open-state: 10s (stay open 10s before half-open)
- permitted-calls-in-half-open: 2 (allow 2 test calls)
