# 06b — Outbox Pattern Demo

## Run
```bash
./gradlew bootRun
```

## Test
```bash
# Create an order (saves order + outbox event atomically)
curl -X POST "localhost:8080/orders?customerId=C1&amount=99.99"

# Check outbox — event published=false initially, then true after 2s
curl localhost:8080/outbox

# Wait 2 seconds, check again — published=true!
sleep 3 && curl localhost:8080/outbox

# Watch logs — poller publishes events every 2 seconds
```

## Key Lesson
Order + event saved in ONE transaction. If Kafka is down, poller retries.
Zero events lost. Guaranteed eventual consistency.
