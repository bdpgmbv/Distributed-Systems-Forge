package com.demo.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * Gateway Global Filters — run on EVERY request.
 * 
 * Filter 1: Correlation ID (adds tracking ID to every request)
 * Filter 2: Logging (logs method, path, status, duration)
 * Filter 3: Simple Auth (checks for Authorization header)
 */
@Slf4j
@Configuration
public class GatewayFilters {

    @Bean @Order(-2)
    public GlobalFilter correlationFilter() {
        return (exchange, chain) -> {
            String corrId = exchange.getRequest().getHeaders()
                .getFirst("X-Correlation-Id");
            if (corrId == null) corrId = UUID.randomUUID().toString().substring(0, 8);
            final String id = corrId;
            var req = exchange.getRequest().mutate()
                .header("X-Correlation-Id", id).build();
            return chain.filter(exchange.mutate().request(req).build())
                .then(Mono.fromRunnable(() ->
                    exchange.getResponse().getHeaders().add("X-Correlation-Id", id)));
        };
    }

    @Bean @Order(-1)
    public GlobalFilter loggingFilter() {
        return (exchange, chain) -> {
            long start = System.currentTimeMillis();
            return chain.filter(exchange).then(Mono.fromRunnable(() -> {
                long ms = System.currentTimeMillis() - start;
                log.info("🚪 {} {} → {} ({}ms)",
                    exchange.getRequest().getMethod(),
                    exchange.getRequest().getPath(),
                    exchange.getResponse().getStatusCode(), ms);
            }));
        };
    }

    @Bean @Order(0)
    public GlobalFilter simpleAuthFilter() {
        return (exchange, chain) -> {
            String path = exchange.getRequest().getPath().value();
            // Skip auth for health and public paths
            if (path.startsWith("/actuator") || path.startsWith("/public")) {
                return chain.filter(exchange);
            }
            String auth = exchange.getRequest().getHeaders().getFirst("Authorization");
            if (auth == null) {
                log.warn("🔒 No Authorization header — request blocked");
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }
            // In real app: validate JWT here
            log.info("🔓 Auth header present: {}...", auth.substring(0, Math.min(20, auth.length())));
            return chain.filter(exchange);
        };
    }
}
