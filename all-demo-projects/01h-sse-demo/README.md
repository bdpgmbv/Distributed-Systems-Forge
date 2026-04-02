# 01h — SSE & Long Polling Demo

## Run
```bash
./gradlew bootRun
```

## Test SSE (Reactive Flux)
```bash
# Auto-streams events every second (Ctrl+C to stop)
curl localhost:8080/sse/stream

# Stock price stream
curl localhost:8080/sse/prices
```

## Test SSE (SseEmitter — non-reactive)
```bash
# Terminal 1: subscribe
curl localhost:8080/sse/emitter

# Terminal 2: push data to subscriber
curl -X POST localhost:8080/sse/emitter/push \
  -H "Content-Type: application/json" -d '{"msg":"hello from server!"}'
# Terminal 1 receives the push!
```

## Test Long Polling
```bash
# Terminal 1: client waits (blocks for up to 30s)
curl localhost:8080/poll

# Terminal 2: push data to waiting client
curl -X POST localhost:8080/poll/push \
  -H "Content-Type: application/json" -d '{"msg":"hello from push"}'
# Terminal 1 receives and returns immediately!
```

## Key Differences
- SSE: one-way server→client, auto-reconnect, plain HTTP
- Long Polling: client waits, server responds when data ready, client re-polls
- WebSocket: bidirectional, special protocol, more complex
