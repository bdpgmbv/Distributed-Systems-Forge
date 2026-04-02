# 03b — Retry + Rate Limiter Demo

## Run
```bash
./gradlew bootRun
```

## Test Retry (watch the logs!)
```bash
# Calls flaky endpoint — retries with exponential backoff
curl localhost:8080/demo/retry/flaky/1

# Calls always-down — retries 3 times then gives up
curl localhost:8080/demo/retry/down/1
```

## Test Rate Limiter
```bash
# Send 10 requests — first 5 succeed, rest get 429
for i in {1..10}; do echo "--- $i ---"; curl -s -w "\nHTTP %{http_code}\n" localhost:8080/demo/ratelimit; done

# Wait 10 seconds and try again
sleep 10 && curl localhost:8080/demo/ratelimit
```

## Config
- Retry: 4 attempts, exponential backoff (500ms → 1s → 2s)
- Rate Limiter: 5 requests per 10 seconds, fail immediately (no wait)
