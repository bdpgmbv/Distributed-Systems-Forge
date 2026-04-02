# 08b — Distributed Tracing Demo
No Zipkin needed — traces logged to console.
## Run
```bash
./gradlew bootRun
```
## Test
```bash
curl "localhost:8080/orders/process?customerId=C42"
# Check logs: [traceId,spanId] on every line. Same traceId = one request.
```