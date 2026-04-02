package com.demo.controller;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Simulates a Strangler Fig gateway routing between monolith and new services.
 *
 * curl localhost:8080/api/users/1     → routed to NEW service
 * curl localhost:8080/api/orders/1    → routed to MONOLITH
 * curl localhost:8080/api/payments/1  → PARALLEL RUN (calls both, compares, returns monolith)
 * curl localhost:8080/routing         → see current routing config
 * curl -X PUT "localhost:8080/routing/orders?target=NEW"   → switch orders to new service!
 */
@Slf4j @RestController
public class GatewayController {
    private final Map<String,String> routing = new LinkedHashMap<>(Map.of("users","NEW","orders","MONOLITH","payments","PARALLEL"));
    private final AtomicInteger matchCount = new AtomicInteger(0);
    private final AtomicInteger mismatchCount = new AtomicInteger(0);

    @GetMapping("/api/{domain}/{id}")
    public Map<String,Object> route(@PathVariable String domain, @PathVariable Long id) {
        String target = routing.getOrDefault(domain, "MONOLITH");
        return switch(target) {
            case "NEW" -> { log.info("🆕 {} → NEW service", domain); yield callNew(domain, id); }
            case "MONOLITH" -> { log.info("🏛️ {} → MONOLITH", domain); yield callMonolith(domain, id); }
            case "PARALLEL" -> parallelRun(domain, id);
            default -> callMonolith(domain, id);
        };
    }

    private Map<String,Object> parallelRun(String domain, Long id) {
        log.info("🔄 {} → PARALLEL RUN (calling both, comparing)", domain);
        Map<String,Object> oldResult = callMonolith(domain, id);
        CompletableFuture.runAsync(() -> {
            Map<String,Object> newResult = callNew(domain, id);
            boolean match = oldResult.get("data").equals(newResult.get("data"));
            if (match) { matchCount.incrementAndGet(); log.info("✅ MATCH for {} {}", domain, id); }
            else { mismatchCount.incrementAndGet(); log.warn("❌ MISMATCH for {} {}: old={} new={}", domain, id, oldResult.get("data"), newResult.get("data")); }
        });
        oldResult.put("routing", "PARALLEL_RUN");
        oldResult.put("note", "Returned monolith response. New service called async for comparison.");
        return oldResult;
    }

    private Map<String,Object> callMonolith(String d, Long id) {
        return new LinkedHashMap<>(Map.of("source","MONOLITH","domain",d,"id",id,"data",d.toUpperCase()+"-"+id+"-legacy"));
    }
    private Map<String,Object> callNew(String d, Long id) {
        return new LinkedHashMap<>(Map.of("source","NEW_SERVICE","domain",d,"id",id,"data",d.toUpperCase()+"-"+id+"-legacy"));
    }

    @GetMapping("/routing")
    public Map<String,Object> getRouting() {
        return Map.of("routes", routing, "parallelStats", Map.of("matches",matchCount.get(),"mismatches",mismatchCount.get()));
    }

    @PutMapping("/routing/{domain}")
    public Map<String,Object> updateRouting(@PathVariable String domain, @RequestParam String target) {
        String old = routing.put(domain, target);
        log.info("🔀 Routing changed: {} from {} → {}", domain, old, target);
        return Map.of("domain", domain, "oldTarget", old, "newTarget", target);
    }
}