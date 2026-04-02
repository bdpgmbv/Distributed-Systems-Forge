# 05a — Kafka Producer Demo
## Start Kafka
```bash
docker compose up -d
```
## Run
```bash
./gradlew bootRun
```
## Test
```bash
curl -X POST localhost:8080/produce/fire-forget
curl -X POST localhost:8080/produce/sync
curl -X POST localhost:8080/produce/async
curl -X POST localhost:8080/produce/with-key?customerId=C-42
```
