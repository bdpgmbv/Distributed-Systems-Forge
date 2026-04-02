package com.demo.controller;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

/**
 * V2: Split name into firstName/lastName, added email.
 * curl localhost:8080/v2/users/1
 */
@RestController @RequestMapping("/v2/users")
public class UserV2Controller {
    @GetMapping("/{id}")
    public Map<String,Object> getUser(@PathVariable Long id) {
        return Map.of("id", id, "firstName", "Alice", "lastName", "Johnson",
            "email", "alice@company.com", "version", "v2",
            "note", "V2 splits name, adds email. V1 consumers still work on /v1/users.");
    }
}