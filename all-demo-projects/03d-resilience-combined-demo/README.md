# 03d — All Resilience Patterns Combined

## Run
```bash
./gradlew bootRun
```

## Test
```bash
# Normal call (all 4 patterns pass)
curl localhost:8080/demo/combined/1

# Flaky calls (watch retry + circuit breaker interaction)
for i in {1..20}; do
  echo -n "Call $i: "
  curl -s localhost:8080/demo/combined/flaky/1 | python3 -c "import sys,json;d=json.load(sys.stdin);print(d.get('status','?'))"
done

# Rate limiter (15 calls, limit=10 per 10s)
for i in {1..15}; do
  echo -n "Call $i: "
  curl -s -w "HTTP %{http_code}" -o /dev/null localhost:8080/demo/combined/1
  echo
done

# Check circuit breaker state
curl -s localhost:8080/actuator/circuitbreakers | python3 -m json.tool
```

## Execution Order
```
Request → RateLimiter (10/10s) → Retry (3x, backoff) → CircuitBreaker (50%) → Bulkhead (5 concurrent) → Call
```
Each retry counts as a CircuitBreaker failure. 3 retries × 5 failed requests = 15 failures → circuit OPENS.
