# 13b — Chaos Engineering Demo

## Run
```bash
./gradlew bootRun
```

## Test
```bash
# WITHOUT circuit breaker — chaos kills some requests
for i in {1..10}; do echo -n "Call $i: "; curl -s localhost:8080/api/data | python3 -c "import sys,json;d=json.load(sys.stdin);print(d.get('status','ERROR'))" 2>/dev/null || echo "FAILED"; done

# WITH circuit breaker — chaos caught by fallback
for i in {1..20}; do echo -n "Call $i: "; curl -s localhost:8080/api/data/resilient | python3 -c "import sys,json;d=json.load(sys.stdin);print(d.get('status','?'))"; done

# Disable chaos
curl -X POST localhost:8080/chaos/disable

# Re-enable
curl -X POST localhost:8080/chaos/enable
```

## What It Tests
- Does your circuit breaker actually trip under random failures?
- Does your fallback return useful degraded data?
- How does your system behave with 500ms-3s random latency?