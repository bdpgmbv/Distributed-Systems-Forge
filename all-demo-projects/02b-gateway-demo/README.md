# 02b — Spring Cloud Gateway Demo

## Architecture
```
Client → Gateway(:8080) → user-service(:8081)
                        → order-service(:8082)
```

## Run
Start backend services first (use 02a project or any REST API on 8081/8082),
then start the gateway:
```bash
./gradlew bootRun
```

## Test
```bash
# Route to user-service (needs auth header)
curl http://localhost:8080/api/users/1 -H "Authorization: Bearer test-token"

# Without auth → 401
curl http://localhost:8080/api/users/1

# Check gateway routes
curl http://localhost:8080/actuator/gateway/routes
```

## What to Observe (check gateway logs)
1. Correlation ID added to every request
2. Logging shows method, path, status, duration
3. Auth filter blocks requests without Authorization header
4. Routes forward to backend services based on path
5. Retry filter retries GET requests 3 times on failure
