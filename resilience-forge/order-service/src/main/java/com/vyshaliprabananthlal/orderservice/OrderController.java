package com.vyshaliprabananthlal.orderservice;

/**
 * 3/2/26 - 09:43
 *
 * @author Vyshali Prabananth Lal
 */

import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderRepository orderRepository;
    private final InventoryClient inventoryClient;

    public OrderController(OrderRepository orderRepository, InventoryClient inventoryClient) {
        this.orderRepository = orderRepository;
        this.inventoryClient = inventoryClient;
    }

    // 1. We protect the POST endpoint with both Bulkhead and RateLimiter
    // Notice we use ResponseEntity now so we can control the HTTP Status codes!
    @PostMapping("/{itemName}")
    @RateLimiter(name = "orderApi", fallbackMethod = "rateLimiterFallback")
    @Bulkhead(name = "orderApi", fallbackMethod = "bulkheadFallback")
    public ResponseEntity<String> createOrder(@PathVariable String itemName) throws ExecutionException, InterruptedException {

        boolean isInStock = inventoryClient.checkInventory(itemName).get();

        if (!isInStock) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("Sorry, we cannot process the order for " + itemName + ". Inventory system is down.");
        }

        orderRepository.save(itemName);
        return ResponseEntity.status(HttpStatus.CREATED).body("Order for " + itemName + " created successfully!");
    }

    // 2. The Rate Limiter Fallback (Triggers when > 5 requests in 60 seconds)
    public ResponseEntity<String> rateLimiterFallback(String itemName, Throwable t) {
        System.out.println("🛑 Rate limit exceeded for item: " + itemName);
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body("Too many requests! Please wait a minute and try again.");
    }

    // 3. The Bulkhead Fallback (Triggers when > 2 users hit this exact method at the exact same millisecond)
    public ResponseEntity<String> bulkheadFallback(String itemName, Throwable t) {
        System.out.println("🚢 Bulkhead full! Rejecting request for item: " + itemName);
        return ResponseEntity.status(HttpStatus.BANDWIDTH_LIMIT_EXCEEDED).body("Server is currently too busy processing other orders. Please try again in a moment.");
    }

    // 4. A simple GET endpoint (Let's rate limit this one too, just for practice!)
    @GetMapping
    @RateLimiter(name = "orderApi", fallbackMethod = "getAllOrdersFallback")
    public ResponseEntity<List<Order>> getAllOrders() {
        return ResponseEntity.ok(orderRepository.findAll());
    }

    // Notice the return type matches the GET endpoint (List<Order>)
    public ResponseEntity<List<Order>> getAllOrdersFallback(Throwable t) {
        System.out.println("🛑 Rate limit exceeded on GET /api/orders");
        // Return an empty list or cached data to safely degrade
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(Collections.emptyList());
    }
}