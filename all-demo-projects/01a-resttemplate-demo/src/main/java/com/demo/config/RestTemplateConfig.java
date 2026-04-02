package com.demo.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.UUID;

/**
 * RestTemplate Configuration — THE MOST IMPORTANT CONFIG IN THIS FILE.
 *
 * 🔴 Rule #1: ALWAYS set timeouts. Default is INFINITE. This is the
 *    #1 cause of cascading failures in microservice architectures.
 *
 * 🔴 Rule #2: Add interceptors for correlation IDs and logging.
 *    Without this, you can't trace requests across services.
 */
@Slf4j
@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder
            // How long to wait for TCP handshake to complete.
            .setConnectTimeout(Duration.ofSeconds(3))
            // How long to wait for the response body after connected.
            .setReadTimeout(Duration.ofSeconds(5))
            // Base URL — all calls are relative to this.
            .rootUri("http://localhost:8080")
            // Interceptor: runs on EVERY outgoing request.
            .additionalInterceptors((request, body, execution) -> {
                String corrId = UUID.randomUUID().toString().substring(0, 8);
                request.getHeaders().set("X-Correlation-Id", corrId);
                long start = System.currentTimeMillis();
                var response = execution.execute(request, body);
                long ms = System.currentTimeMillis() - start;
                log.info("📡 [RestTemplate] {} {} → {} ({}ms) [corr={}]",
                    request.getMethod(), request.getURI(),
                    response.getStatusCode(), ms, corrId);
                return response;
            })
            .build();
    }
}
