# 04b — Feature Flags Demo (DB-backed, H2 in-memory)

## Run
```bash
./gradlew bootRun
```

## Test
```bash
# List all flags
curl localhost:8080/flags

# Check flag for specific user (deterministic hash)
curl "localhost:8080/flags/check?flag=new-ui&userId=user-42"
curl "localhost:8080/flags/check?flag=new-ui&userId=user-88"

# See which dashboard a user gets
curl "localhost:8080/dashboard?userId=user-42"
curl "localhost:8080/dashboard?userId=user-7"

# Change rollout percentage (simulate gradual rollout)
curl -X PUT "localhost:8080/flags/new-ui/rollout?percent=50"
curl "localhost:8080/dashboard?userId=user-42"   # might change!

# Kill switch — disable instantly
curl -X PUT localhost:8080/flags/new-ui/toggle

# Create new flag
curl -X POST localhost:8080/flags -H "Content-Type: application/json" \
  -d '{"name":"holiday-banner","enabled":true,"rolloutPercentage":100}'

# H2 console (see the DB)
open http://localhost:8080/h2
# JDBC URL: jdbc:h2:mem:flags
```
