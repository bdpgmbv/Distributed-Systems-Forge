# 12b — API Versioning Demo

## Run
```bash
./gradlew bootRun
```

## Test URI Versioning (most common)
```bash
curl localhost:8080/v1/users/1     # V1: { name: "Alice Johnson" }
curl localhost:8080/v2/users/1     # V2: { firstName: "Alice", lastName: "Johnson", email: "..." }
curl localhost:8080/v1/users       # V1 list
```

## Test Header Versioning
```bash
curl localhost:8080/api/users/1 -H "X-API-Version: 1"   # V1
curl localhost:8080/api/users/1 -H "X-API-Version: 2"   # V2
```

## Key Lesson
Both versions run simultaneously. Old consumers keep using V1. New consumers use V2.
No breaking changes. Deprecate V1 after 6 months.