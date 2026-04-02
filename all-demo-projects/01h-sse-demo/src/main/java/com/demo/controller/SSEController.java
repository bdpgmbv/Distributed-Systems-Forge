package com.demo.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.*;

/**
 * SSE + Long Polling Demo.
 *
 * curl localhost:8080/sse/stream          — SSE with Flux (reactive)
 * curl localhost:8080/sse/prices          — stock price SSE stream
 * curl localhost:8080/sse/emitter         — SSE with SseEmitter (non-reactive)
 * curl localhost:8080/poll                — long polling (blocks until data)
 * curl -X POST localhost:8080/poll/push -H "Content-Type: application/json" -d '{"msg":"hello"}'
 */
@Slf4j
@RestController
public class SSEController {

    // ==========================================
    // SSE with Flux (reactive) — recommended approach
    // ==========================================
    @GetMapping(value = "/sse/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<Map<String,Object>>> stream() {
        log.info("SSE: client subscribed to /sse/stream");
        return Flux.interval(Duration.ofSeconds(1))
            .take(30)
            .map(i -> ServerSentEvent.<Map<String,Object>>builder()
                .id(String.valueOf(i))
                .event("heartbeat")
                .data(Map.of("seq", i, "time", Instant.now().toString(), "msg", "Event #" + i))
                .retry(Duration.ofSeconds(5))
                .build());
    }

    // ==========================================
    // SSE stock prices
    // ==========================================
    @GetMapping(value = "/sse/prices", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<Map<String,Object>>> prices() {
        log.info("SSE: client subscribed to /sse/prices");
        return Flux.interval(Duration.ofSeconds(1)).take(60).map(i -> {
            double price = 150.0 + (Math.random() * 20 - 10);
            return ServerSentEvent.<Map<String,Object>>builder()
                .id(String.valueOf(i)).event("price")
                .data(Map.of("symbol", "AAPL", "price", Math.round(price * 100.0) / 100.0, "time", Instant.now().toString()))
                .build();
        });
    }

    // ==========================================
    // SSE with SseEmitter (non-reactive — for servlet apps)
    // ==========================================
    private final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();

    @GetMapping("/sse/emitter")
    public SseEmitter emitter() {
        SseEmitter e = new SseEmitter(0L);
        emitters.add(e);
        e.onCompletion(() -> emitters.remove(e));
        e.onTimeout(() -> emitters.remove(e));
        log.info("SSE emitter: client subscribed ({} total)", emitters.size());
        return e;
    }

    /** Push to all SseEmitter subscribers: curl -X POST localhost:8080/sse/emitter/push -d '{"msg":"hello"}' -H "Content-Type: application/json" */
    @PostMapping("/sse/emitter/push")
    public Map<String,Object> pushToEmitters(@RequestBody Map<String,String> body) {
        List<SseEmitter> dead = new ArrayList<>();
        emitters.forEach(e -> { try { e.send(SseEmitter.event().name("push").data(body)); } catch(Exception ex) { dead.add(e); } });
        emitters.removeAll(dead);
        return Map.of("pushed_to", emitters.size(), "dead_removed", dead.size());
    }

    // ==========================================
    // LONG POLLING — client waits until data arrives
    // ==========================================
    private final BlockingQueue<Map<String,String>> queue = new LinkedBlockingQueue<>();

    @GetMapping("/poll")
    public DeferredResult<ResponseEntity<Map<String,String>>> poll() {
        log.info("Long poll: client waiting for data...");
        DeferredResult<ResponseEntity<Map<String,String>>> result = new DeferredResult<>(30000L);
        result.onTimeout(() -> result.setResult(ResponseEntity.noContent().build()));
        CompletableFuture.runAsync(() -> {
            try {
                Map<String,String> data = queue.poll(30, TimeUnit.SECONDS);
                if (data != null) result.setResult(ResponseEntity.ok(data));
            } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        });
        return result;
    }

    /** Push data to waiting long-poll client: curl -X POST localhost:8080/poll/push -H "Content-Type: application/json" -d '{"msg":"hello from push"}' */
    @PostMapping("/poll/push")
    public Map<String,String> pushToPoll(@RequestBody Map<String,String> data) {
        queue.offer(data);
        log.info("Long poll: pushed data, waiting client will receive it");
        return Map.of("status", "pushed", "note", "Any waiting /poll client will now receive this");
    }
}
