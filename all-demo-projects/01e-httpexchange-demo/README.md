# 01e — @HttpExchange Demo (Spring 6.1 Native Declarative)

Like Feign but BUILT INTO Spring. Zero extra dependencies.

## Run
```bash
./gradlew bootRun
```

## Test
```bash
curl localhost:8080/demo/users/1
curl localhost:8080/demo/users
curl -X POST localhost:8080/demo/users -H "Content-Type: application/json" \
  -d '{"name":"HttpExchange","email":"he@test.com"}'
```

## Key Difference from Feign
- No spring-cloud dependency needed
- Backed by WebClient or RestClient (you choose)
- No built-in fallback (use Resilience4j @CircuitBreaker on caller)
- Spring's FUTURE direction for declarative HTTP clients
