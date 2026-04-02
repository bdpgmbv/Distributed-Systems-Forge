# 04a — Config Server + Dynamic Refresh Demo

## Run (2 terminals)
```bash
# T1: Start Config Server (serves config from config-repo/ folder)
./gradlew bootRun

# T2: Start the client service
./gradlew bootRun --args='--spring.profiles.active=client'
```

## Test
```bash
# See what Config Server serves
curl localhost:8888/demo-service/default
curl localhost:8888/demo-service/dev

# See config values in the client
curl localhost:8080/demo/config
curl localhost:8080/demo/features

# DYNAMIC REFRESH — change config without restart!
# 1. Edit config-repo/demo-service.yml (change greeting or feature flags)
# 2. Refresh the client:
curl -X POST localhost:8080/actuator/refresh
# 3. See updated values:
curl localhost:8080/demo/config
```

## Key Lesson
Config Server serves config from a central location. @RefreshScope beans get recreated on /actuator/refresh. Zero downtime config changes.
