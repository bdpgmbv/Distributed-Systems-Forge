# 12a — Idempotency Demo

## Run
```bash
./gradlew bootRun
```

## Test: The Double-Charge Problem
```bash
# WITHOUT idempotency key (DANGEROUS!)
curl -X POST "localhost:8080/payments?customerId=C1&amount=99.99"
curl -X POST "localhost:8080/payments?customerId=C1&amount=99.99"
curl localhost:8080/payments
# → TWO payments! Customer charged twice!

# WITH idempotency key (SAFE!)
curl -X POST "localhost:8080/payments?customerId=C2&amount=49.99" -H "Idempotency-Key: pay-xyz-789"
curl -X POST "localhost:8080/payments?customerId=C2&amount=49.99" -H "Idempotency-Key: pay-xyz-789"
curl -X POST "localhost:8080/payments?customerId=C2&amount=49.99" -H "Idempotency-Key: pay-xyz-789"
curl localhost:8080/payments
# → Only ONE payment for C2! All retries returned stored result.

# See stored keys
curl localhost:8080/idempotency-keys
```