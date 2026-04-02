# 01a — RestTemplate Demo (Legacy Synchronous Client)

## Run
```bash
./gradlew bootRun
```

## Test Every Operation
```bash
# GET single user
curl http://localhost:8080/demo/users/1

# GET all users (uses ParameterizedTypeReference for List<T>)
curl http://localhost:8080/demo/users

# POST create user
curl -X POST http://localhost:8080/demo/users \
  -H "Content-Type: application/json" \
  -d '{"name":"NewUser","email":"new@test.com"}'

# PUT update user
curl -X PUT http://localhost:8080/demo/users/1 \
  -H "Content-Type: application/json" \
  -d '{"name":"Updated","email":"updated@test.com"}'

# SEARCH with query params (UriComponentsBuilder)
curl "http://localhost:8080/demo/users/search?name=alice&page=0"

# Custom headers (auth + correlation ID)
curl http://localhost:8080/demo/users/1/secure

# TIMEOUT test — fast (2s delay, succeeds)
curl http://localhost:8080/demo/timeout/fast

# TIMEOUT test — slow (10s delay, TIMES OUT after 5s!)
curl http://localhost:8080/demo/timeout/slow

# ERROR handling (user not found)
curl http://localhost:8080/demo/users/999/handled
```

## Key Files
- `RestTemplateConfig.java` — timeout + interceptor config (READ THIS FIRST)
- `DemoController.java` — every RestTemplate operation with comments
- `UserController.java` — embedded "remote" service

## Key Lessons
1. ALWAYS set timeouts (default is infinite)
2. Use ParameterizedTypeReference for List<T> (or you get LinkedHashMap)
3. exchange() is the universal method (any HTTP method + headers)
4. UriComponentsBuilder for dynamic URLs (never concatenate strings)
5. Thread BLOCKS during every call — this is why it's deprecated
