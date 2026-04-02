# 02c — Load Balancer Demo

## How It Works
application.yml defines 3 instances of "user-service" at different ports.
Spring Cloud LoadBalancer distributes calls using round-robin.

## Run
```bash
# Start this app
./gradlew bootRun

# (Optional) Start a user-service on :8081 for real responses
# Otherwise you'll see connection errors — that's fine, the load balancer
# selection still happens and is logged.
```

## Test
```bash
curl http://localhost:8080/lb/call       # single call
curl http://localhost:8080/lb/call10     # 10 calls — see distribution
```

## What to Observe
- `application.yml` defines 3 instances with different weights and zones
- @LoadBalanced RestTemplate resolves "http://user-service" via load balancer
- Default strategy: round-robin across all healthy instances
- Logs show which instance was selected for each call
