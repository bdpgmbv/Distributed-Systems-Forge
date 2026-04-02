# 08a — Metrics & Health Check Demo
## Run
```bash
./gradlew bootRun
```
## Test
```bash
for i in {1..10}; do curl -s "localhost:8080/orders/create?customerId=C$i" > /dev/null; done
curl localhost:8080/actuator/prometheus | grep orders
curl localhost:8080/actuator/health
curl localhost:8080/actuator/health/liveness
curl localhost:8080/actuator/health/readiness
curl -X POST localhost:8080/health/toggle   # simulate DOWN
curl localhost:8080/actuator/health          # see it DOWN
curl -X POST localhost:8080/health/toggle   # restore
```