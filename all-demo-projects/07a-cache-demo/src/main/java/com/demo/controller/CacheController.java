package com.demo.controller;
import com.demo.model.Product;
import com.demo.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.Map;

/**
 * curl localhost:8080/products/1          — first: 500ms (DB). second: <1ms (CACHE!)
 * curl localhost:8080/products/1/timed    — shows elapsed time
 * curl -X PUT "localhost:8080/products/1?name=Gaming+Laptop&price=1299.99"  — updates DB + cache
 * curl -X DELETE localhost:8080/products/1  — evicts from cache
 * curl -X POST localhost:8080/products/clear-cache  — evicts all
 */
@RestController @RequestMapping("/products") @RequiredArgsConstructor
public class CacheController {
    private final ProductService svc;

    @GetMapping("/{id}")
    public Product get(@PathVariable Long id) { return svc.getProduct(id); }

    @GetMapping("/{id}/timed")
    public Map<String,Object> getTimed(@PathVariable Long id) {
        long start = System.currentTimeMillis();
        Product p = svc.getProduct(id);
        long ms = System.currentTimeMillis() - start;
        return Map.of("product", p, "elapsed_ms", ms,
            "note", ms > 100 ? "CACHE MISS — hit DB (500ms simulated)" : "CACHE HIT — from Caffeine (<1ms!)");
    }

    @PutMapping("/{id}")
    public Product update(@PathVariable Long id, @RequestParam String name, @RequestParam BigDecimal price) {
        return svc.updateProduct(id, name, price);
    }

    @DeleteMapping("/{id}")
    public Map<String,String> delete(@PathVariable Long id) { svc.deleteProduct(id); return Map.of("deleted", id.toString()); }

    @PostMapping("/clear-cache")
    public Map<String,String> clear() { svc.clearCache(); return Map.of("status", "cache cleared"); }
}
