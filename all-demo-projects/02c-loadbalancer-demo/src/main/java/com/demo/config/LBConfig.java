package com.demo.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * @LoadBalanced makes RestTemplate resolve service names via the load balancer.
 * "http://user-service/api/users/1" → resolved to one of the configured instances.
 */
@Configuration
public class LBConfig {
    @Bean @LoadBalanced
    public RestTemplate restTemplate() { return new RestTemplate(); }
}
