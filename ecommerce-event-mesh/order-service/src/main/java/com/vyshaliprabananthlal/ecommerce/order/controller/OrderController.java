package com.vyshaliprabananthlal.ecommerce.order.controller;

/**
 * 3/1/26 - 21:05
 *
 * @author Vyshali Prabananth Lal
 */

import com.vyshaliprabananthlal.ecommerce.order.saga.OrderSagaOrchestrator;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderSagaOrchestrator sagaOrchestrator;

    public OrderController(OrderSagaOrchestrator sagaOrchestrator) {
        this.sagaOrchestrator = sagaOrchestrator;
    }

    // A simple DTO for the incoming request
    public record CreateOrderRequest(String productId, BigDecimal amount) {}

    @PostMapping
    public ResponseEntity<String> placeOrder(@RequestBody CreateOrderRequest request) {
        String orderId = UUID.randomUUID().toString();

        // Hand off to the Saga Orchestrator!
        sagaOrchestrator.startSaga(orderId, request.productId(), request.amount());

        return ResponseEntity.ok("Order " + orderId + " is PENDING. Saga started!");
    }
}
