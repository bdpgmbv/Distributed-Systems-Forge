# 11a — Docker & Kubernetes Demo

## Run Locally (no Docker needed)
```bash
./gradlew bootRun
curl localhost:8080/
curl localhost:8080/actuator/health/liveness
curl localhost:8080/actuator/health/readiness
```

## Build Docker Image
```bash
docker build -t k8s-demo .
docker run -p 8080:8080 k8s-demo
```

## Deploy to K8s
```bash
kubectl apply -f k8s/
kubectl get pods
kubectl logs -f deployment/k8s-demo
```

## Key Files
- `Dockerfile`: Multi-stage build (450MB → 180MB)
- `k8s/deployment.yml`: 3 replicas, rolling update, liveness/readiness/startup probes
- `k8s/hpa.yml`: Auto-scale 3→20 pods at 70% CPU
- `k8s/service.yml`: ClusterIP service

## Key Lessons
- Liveness: ONLY check JVM alive. NEVER DB/Redis (or K8s restarts during DB outage)
- Readiness: check ALL dependencies. Fail = stop sending traffic
- Graceful shutdown: server.shutdown=graceful + lifecycle.timeout=30s
- MaxRAMPercentage=75.0: let JVM use 75% of container limit