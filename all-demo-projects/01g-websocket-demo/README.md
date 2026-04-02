# 01g — WebSocket Demo (STOMP + Raw)

## Run
```bash
./gradlew bootRun
```

## Test in Browser
Open http://localhost:8080 in TWO browser tabs.
Send messages — both tabs see them in real-time!

## Test Server Push
```bash
curl http://localhost:8080/push
# All connected WebSocket clients receive the alert
```

## What's Demonstrated
- STOMP broadcast (/topic/chat — all subscribers get it)
- STOMP private messages (/user/{name}/queue/private)
- Raw WebSocket (no STOMP, manual session management)
- Server-initiated push (triggered by REST call)
