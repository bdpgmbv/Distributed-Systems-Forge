package com.demo.controller;
import com.demo.service.ChaosMonkey;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

/**
 * curl localhost:8080/api/data              — chaos monkey may inject latency or exception
 * curl localhost:8080/api/data/resilient    — same + circuit breaker protection
 * curl -X POST localhost:8080/chaos/disable — turn off chaos
 * curl -X POST localhost:8080/chaos/enable  — turn on chaos
 *
 * Test: for i in {1..20}; do curl -s localhost:8080/api/data/resilient | python3 -c "import sys,json;d=json.load(sys.stdin);print(d.get('status','?'))"; done
 */
@Slf4j @RestController @RequiredArgsConstructor
public class DemoController {
    private final ChaosMonkey chaos;

    @GetMapping("/api/data")
    public Map<String,Object> getData() {
        chaos.maybeAttack("getData");
        return Map.of("status","SUCCESS","data","important-data","note","Chaos monkey may have delayed or killed this!");
    }

    @GetMapping("/api/data/resilient")
    @CircuitBreaker(name="backend", fallbackMethod="fallback")
    public Map<String,Object> getDataResilient() {
        chaos.maybeAttack("getDataResilient");
        return Map.of("status","SUCCESS","data","important-data");
    }

    private Map<String,Object> fallback(Exception e) {
        return Map.of("status","FALLBACK","reason",e.getMessage(),"note","Circuit breaker caught the chaos!");
    }

    @PostMapping("/chaos/enable") public Map<String,Object> enable() { chaos.enabled.set(true); return Map.of("chaos","ENABLED"); }
    @PostMapping("/chaos/disable") public Map<String,Object> disable() { chaos.enabled.set(false); return Map.of("chaos","DISABLED"); }
}