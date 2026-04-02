# 01b — WebClient Demo (Reactive Non-Blocking)

## Run
```bash
./gradlew bootRun
```

## Test
```bash
curl localhost:8080/demo/users/1         # basic GET (Mono)
curl localhost:8080/demo/users           # GET all (Flux)
curl localhost:8080/demo/parallel        # 3 calls AT ONCE — check total_ms!
curl localhost:8080/demo/timeout         # 10s endpoint with 3s timeout
curl localhost:8080/demo/retry           # retries 3x with exponential backoff
```

## Key Lessons
1. Thread NEVER blocks — returns Mono/Flux immediately
2. Mono.zip() for parallel calls — 3x faster than sequential
3. Configure maxInMemorySize (default 256KB crashes on big responses!)
4. Configure ConnectionProvider for production connection pooling
5. .timeout() is per-call, separate from connection timeout
6. .retryWhen() with .filter() — only retry retryable errors
