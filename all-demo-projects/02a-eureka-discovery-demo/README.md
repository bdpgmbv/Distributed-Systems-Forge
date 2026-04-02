# 02a — Eureka Service Discovery Demo

## Run (3 terminals)
```bash
# T1: Eureka Server (dashboard at http://localhost:8761)
./gradlew bootRun

# T2: user-service (registers on :8081)
./gradlew bootRun --args='--spring.profiles.active=user'

# T3: order-service (registers on :8082, discovers user-service)
./gradlew bootRun --args='--spring.profiles.active=order'
```

## Test
```bash
open http://localhost:8761                          # Eureka dashboard
curl http://localhost:8082/orders/for-user/1        # Eureka discovery in action!
curl http://localhost:8082/orders/discover           # See all registered services
curl http://localhost:8081/api/users                 # Direct call to user-service
```

## What to Observe
1. Dashboard shows both services registered
2. `/orders/for-user/1` — Feign discovers user-service through Eureka
3. Kill user-service → dashboard shows DOWN after ~15s
4. `/orders/for-user/1` again → error (no instances)
