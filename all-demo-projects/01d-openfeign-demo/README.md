# 01d — OpenFeign Demo (Declarative REST Client)

## Run
```bash
./gradlew bootRun
```

## Test
```bash
curl localhost:8080/demo/users/1
curl localhost:8080/demo/users
curl "localhost:8080/demo/users/search?name=alice"
curl -X POST localhost:8080/demo/users -H "Content-Type: application/json" \
  -d '{"name":"Feign","email":"f@test.com"}'
```

## Test Fallback (simulate service down)
The embedded UserController always works, but the fallback code is ready.
To test: change the @FeignClient url to a dead port like `http://localhost:9999`,
rebuild, and call the endpoints — you'll see fallback responses.
