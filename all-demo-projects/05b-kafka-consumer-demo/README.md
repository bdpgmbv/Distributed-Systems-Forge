# 05b — Kafka Consumer Demo
## Start Kafka & Run
```bash
docker compose up -d
./gradlew bootRun
```
## Test
```bash
curl -X POST "localhost:8080/send?count=10"    # produce messages
curl localhost:8080/consumed                    # see what was consumed
```
