package com.demo.controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

@RestController
public class FallbackController {
    @GetMapping("/fallback/users")
    public Map<String,String> userFallback() {
        return Map.of("message","User service temporarily unavailable","status","DEGRADED");
    }
    @GetMapping("/fallback/orders")
    public Map<String,String> orderFallback() {
        return Map.of("message","Order service temporarily unavailable");
    }
}
