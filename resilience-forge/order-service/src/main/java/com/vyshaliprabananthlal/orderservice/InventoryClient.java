package com.vyshaliprabananthlal.orderservice;

/**
 * 3/2/26 - 09:54
 *
 * @author Vyshali Prabananth Lal
 */

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class InventoryClient {

    // Stacking the resilience patterns!
    @Retry(name = "inventoryService")
    @CircuitBreaker(name = "inventoryService", fallbackMethod = "fallbackCheckInventory")
    @TimeLimiter(name = "inventoryService") // Requires CompletableFuture
    public CompletableFuture<Boolean> checkInventory(String itemName) {

        return CompletableFuture.supplyAsync(() -> {
            System.out.println("Attempting to call external Inventory Service for: " + itemName);

            // Simulating a slow, failing service:
            // 1. First, we simulate a delay that will trigger our 2-second TimeLimiter
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            // 2. Then we throw an error to trigger the Retry/Circuit Breaker
            throw new RuntimeException("Connection Refused! Inventory Service is DOWN!");
        });
    }

    // Notice the Fallback now also returns a CompletableFuture to match the main method!
    public CompletableFuture<Boolean> fallbackCheckInventory(String itemName, Throwable throwable) {
        System.out.println("⚠️ FALLBACK TRIGGERED for " + itemName);
        System.out.println("⚠️ Reason: " + throwable.getMessage());

        return CompletableFuture.completedFuture(false);
    }
}