package com.demo.controller;
import com.demo.model.Payment;
import com.demo.service.IdempotencyService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;

/**
 * WITHOUT idempotency key: curl -X POST "localhost:8080/payments?customerId=C1&amount=99.99"
 *   → Each call creates a NEW payment (double-charge risk!)
 *
 * WITH idempotency key: curl -X POST "localhost:8080/payments?customerId=C1&amount=99.99" -H "Idempotency-Key: pay-abc-123"
 *   → First call: processes payment, stores result
 *   → Second call with SAME key: returns stored result, NO re-processing!
 */
@Slf4j @RestController @RequiredArgsConstructor
public class PaymentController {
    private final IdempotencyService idempotency;
    private final EntityManager em;
    private final ObjectMapper json;

    @PostMapping("/payments")
    @Transactional
    public Map<String,Object> createPayment(@RequestParam String customerId, @RequestParam BigDecimal amount,
            @RequestHeader(value="Idempotency-Key", required=false) String key) throws Exception {

        // WITHOUT key: always process (dangerous!)
        if (key == null) {
            Payment p = processPayment(customerId, amount);
            log.warn("⚠️ No idempotency key! Payment {} processed. Retry = DOUBLE CHARGE!", p.getId());
            return Map.of("paymentId", p.getId(), "status", "PROCESSED",
                "warning", "No Idempotency-Key header! Retrying this request will charge AGAIN!");
        }

        // WITH key: check if already processed
        var existing = idempotency.find(key);
        if (existing.isPresent()) {
            log.info("🔁 REPLAY: key={} already processed. Returning stored result.", key);
            return json.readValue(existing.get().getResponseBody(), Map.class);
        }

        // First time: process and store
        Payment p = processPayment(customerId, amount);
        Map<String,Object> response = Map.of("paymentId", p.getId(), "status", "PROCESSED",
            "idempotencyKey", key, "note", "Retry with same key returns this EXACT response. No re-charge.");
        idempotency.save(key, json.writeValueAsString(response), 200);
        return response;
    }

    private Payment processPayment(String customerId, BigDecimal amount) {
        Payment p = Payment.builder().customerId(customerId).amount(amount).processedAt(Instant.now()).build();
        em.persist(p); em.flush();
        log.info("💳 Payment {} processed: {} charged {}", p.getId(), customerId, amount);
        return p;
    }

    @GetMapping("/payments")
    public List<Payment> all() { return em.createQuery("FROM Payment", Payment.class).getResultList(); }

    @GetMapping("/idempotency-keys")
    public List<Map<String,Object>> keys() {
        return em.createQuery("FROM IdempotencyRecord", com.demo.model.IdempotencyRecord.class).getResultList().stream()
            .map(r -> Map.<String,Object>of("key", r.getIdempotencyKey(), "status", r.getResponseStatus(), "createdAt", r.getCreatedAt().toString())).toList();
    }
}