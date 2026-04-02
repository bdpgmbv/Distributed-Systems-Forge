# 06a — Saga Choreography Demo

Simulates Order → Payment → Inventory saga using Spring events (simulating Kafka).

## Run
```bash
./gradlew bootRun
```

## Test Happy Path
```bash
curl -X POST "localhost:8080/orders?customerId=C1&amount=99"
# → CONFIRMED (payment OK, stock available)
```

## Test Payment Failure (amount > 500)
```bash
curl -X POST "localhost:8080/orders?customerId=C2&amount=999"
# → CANCELLED (payment failed → order compensated)
```

## Test Inventory Failure (run 6+ times to exhaust stock=5)
```bash
for i in {1..7}; do curl -s -X POST "localhost:8080/orders?customerId=C$i&amount=50" | python3 -m json.tool; done
# Orders 1-5: CONFIRMED. Orders 6-7: CANCELLED (out of stock → refund triggered!)
```

## See the Full Saga Trail
```bash
curl localhost:8080/orders     # all orders with final status
curl localhost:8080/events     # every event in the saga chain
```
