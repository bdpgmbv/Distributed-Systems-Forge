# 10a — JWT Security Demo
## Run
```bash
./gradlew bootRun
```
## Test
```bash
# Get token
TOKEN=$(curl -s -X POST "localhost:8080/auth/login?username=alice&roles=USER,ADMIN" | python3 -c "import sys,json;print(json.load(sys.stdin)['token'])")
# Authenticated
curl localhost:8080/api/profile -H "Authorization: Bearer $TOKEN"
# Admin only
curl localhost:8080/api/admin -H "Authorization: Bearer $TOKEN"
# No token → 403
curl localhost:8080/api/profile
# User without ADMIN
TOKEN2=$(curl -s -X POST "localhost:8080/auth/login?username=bob&roles=USER" | python3 -c "import sys,json;print(json.load(sys.stdin)['token'])")
curl localhost:8080/api/admin -H "Authorization: Bearer $TOKEN2"  # 403!
```