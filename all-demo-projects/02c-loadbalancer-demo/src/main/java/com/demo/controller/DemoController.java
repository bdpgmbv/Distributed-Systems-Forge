package com.demo.controller;

import com.demo.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.*;

/**
 * Load Balancer Demo — calls "user-service" which resolves to one of 3 instances.
 *
 * curl http://localhost:8080/lb/call      — one call (see which instance was chosen)
 * curl http://localhost:8080/lb/call10    — 10 calls (see round-robin distribution)
 */
@Slf4j @RestController @RequestMapping("/lb") @RequiredArgsConstructor
public class DemoController {

    private final RestTemplate restTemplate; // @LoadBalanced

    @GetMapping("/call")
    public Map<String, Object> callOnce() {
        log.info("Calling user-service via load balancer...");
        try {
            User user = restTemplate.getForObject(
                "http://user-service/api/users/1", User.class);
            return Map.of("user", user, "note",
                "Load balancer picked one of the configured instances (round-robin)");
        } catch (Exception e) {
            return Map.of("error", e.getMessage(),
                "note", "Expected! Start user-service on :8081 first, or just observe the load balancer logs.");
        }
    }

    @GetMapping("/call10")
    public List<Map<String, Object>> call10Times() {
        List<Map<String, Object>> results = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            try {
                User user = restTemplate.getForObject(
                    "http://user-service/api/users/1", User.class);
                results.add(Map.of("call", i + 1, "status", "OK", "user", user.getName()));
            } catch (Exception e) {
                results.add(Map.of("call", i + 1, "status", "FAIL", "error", e.getMessage()));
            }
        }
        log.info("10 calls completed. Check which instances were hit.");
        return results;
    }
}
