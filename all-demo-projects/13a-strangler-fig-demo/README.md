# 13a — Strangler Fig Migration Demo

## Run
```bash
./gradlew bootRun
```

## Test
```bash
# See routing config
curl localhost:8080/routing

# Users → NEW service (already migrated)
curl localhost:8080/api/users/1

# Orders → MONOLITH (not yet migrated)
curl localhost:8080/api/orders/1

# Payments → PARALLEL RUN (calls both, compares, returns monolith)
curl localhost:8080/api/payments/1

# Switch orders to new service (live migration!)
curl -X PUT "localhost:8080/routing/orders?target=NEW"
curl localhost:8080/api/orders/1    # Now goes to NEW!

# Switch payments from parallel to new (confident after 0 mismatches)
curl -X PUT "localhost:8080/routing/payments?target=NEW"
```

## Migration Phases
1. MONOLITH → all traffic to old system
2. PARALLEL → call both, compare, return old (build confidence)
3. NEW → switch traffic to new service
4. Repeat per domain until monolith is gone