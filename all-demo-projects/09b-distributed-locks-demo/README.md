# 09b — Distributed Locks Demo
## Run
```bash
./gradlew bootRun
```
## Test
```bash
curl -X POST localhost:8080/race/safe     # 20 concurrent, WITH lock → stock=0
curl -X POST localhost:8080/race/unsafe   # 20 concurrent, NO lock → stock NEGATIVE!
```