# 01c — RestClient Demo (Spring Boot 3.2+ Modern Sync)

The OFFICIAL RestTemplate replacement. Same sync model, fluent API.

## Run
```bash
./gradlew bootRun
```

## Test
```bash
curl localhost:8080/demo/users/1          # basic GET
curl localhost:8080/demo/users            # GET list
curl -X POST localhost:8080/demo/users -H "Content-Type: application/json" \
  -d '{"name":"New","email":"new@test.com"}'
curl localhost:8080/demo/users/1/full     # ResponseEntity (status+headers+body)
curl localhost:8080/demo/users/999/safe   # exchange() graceful error handling
```
