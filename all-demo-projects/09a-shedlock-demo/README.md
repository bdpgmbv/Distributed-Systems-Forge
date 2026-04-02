# 09a — ShedLock Demo
## Run
```bash
./gradlew bootRun
```
## Test
```bash
# Wait 10-30s then:
curl localhost:8080/executions    # which jobs ran
curl localhost:8080/locks         # shedlock table
```
Without ShedLock: 3 pods = 3 executions. With ShedLock: exactly 1.