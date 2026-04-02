package com.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

/**
 * Registers the @HttpExchange interface as a Spring bean.
 * Uses WebClient as the backend (could also use RestClient).
 */
@Configuration
public class HttpExchangeConfig {
    @Bean
    public UserApiClient userApiClient(WebClient.Builder builder) {
        WebClient client = builder.baseUrl("http://localhost:8080").build();
        return HttpServiceProxyFactory.builderFor(WebClientAdapter.create(client))
            .build().createClient(UserApiClient.class);
    }
}
