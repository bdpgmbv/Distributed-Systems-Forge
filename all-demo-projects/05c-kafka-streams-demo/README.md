# 05c — Kafka Streams (Word Count)
## Start Kafka & Run
```bash
docker compose up -d && ./gradlew bootRun
```
## Test
```bash
curl -X POST "localhost:8080/send?text=hello world hello kafka"
curl -X POST "localhost:8080/send?text=kafka streams are powerful"
curl localhost:8080/counts
curl localhost:8080/counts/hello
curl localhost:8080/counts/kafka
```
