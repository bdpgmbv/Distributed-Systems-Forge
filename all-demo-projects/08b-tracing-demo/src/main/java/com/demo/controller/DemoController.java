package com.demo.controller;
import io.micrometer.tracing.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@Slf4j @RestController @RequiredArgsConstructor
public class DemoController {
    private final Tracer tracer;

    @GetMapping("/orders/process")
    public Map<String,Object> process(@RequestParam String customerId) throws Exception {
        log.info("Request for {}", customerId);

        Span dbSpan = tracer.nextSpan().name("db-query").start();
        try (Tracer.SpanInScope ws = tracer.withSpan(dbSpan)) {
            dbSpan.tag("customerId", customerId);
            log.info("DB query..."); Thread.sleep(50);
        } finally { dbSpan.end(); }

        Span enrichSpan = tracer.nextSpan().name("enrich").start();
        try (Tracer.SpanInScope ws = tracer.withSpan(enrichSpan)) {
            log.info("Enriching..."); Thread.sleep(30);
        } finally { enrichSpan.end(); }

        String traceId = tracer.currentSpan() != null ? tracer.currentSpan().context().traceId() : "none";
        return Map.of("orderId","ORD-"+System.currentTimeMillis(),"traceId",traceId,
            "note","Check logs — every line has [traceId,spanId]!");
    }
}