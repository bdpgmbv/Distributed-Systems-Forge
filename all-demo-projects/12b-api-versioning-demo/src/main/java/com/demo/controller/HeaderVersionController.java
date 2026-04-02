package com.demo.controller;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

/**
 * Header-based versioning: X-API-Version header selects version.
 * curl localhost:8080/api/users/1 -H "X-API-Version: 1"
 * curl localhost:8080/api/users/1 -H "X-API-Version: 2"
 */
@RestController @RequestMapping("/api/users")
public class HeaderVersionController {
    @GetMapping(value="/{id}", headers="X-API-Version=1")
    public Map<String,Object> v1(@PathVariable Long id) {
        return Map.of("id", id, "name", "Alice Johnson", "strategy", "HEADER", "version", "v1");
    }
    @GetMapping(value="/{id}", headers="X-API-Version=2")
    public Map<String,Object> v2(@PathVariable Long id) {
        return Map.of("id", id, "firstName", "Alice", "lastName", "Johnson", "email", "alice@co.com", "strategy", "HEADER", "version", "v2");
    }
}