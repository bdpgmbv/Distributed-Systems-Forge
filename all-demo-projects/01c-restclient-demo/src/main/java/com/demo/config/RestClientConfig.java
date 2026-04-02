package com.demo.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

@Slf4j
@Configuration
public class RestClientConfig {
    @Bean
    public RestClient restClient(RestClient.Builder builder) {
        return builder.baseUrl("http://localhost:8080")
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .requestInterceptor((req, body, exec) -> {
                long start = System.currentTimeMillis();
                var resp = exec.execute(req, body);
                log.info("📡 [RestClient] {} {} → {} ({}ms)",
                    req.getMethod(), req.getURI(), resp.getStatusCode(),
                    System.currentTimeMillis() - start);
                return resp;
            }).build();
    }
}
