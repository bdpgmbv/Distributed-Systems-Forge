package com.demo.controller;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

/**
 * V1: Simple user with combined name field.
 * curl localhost:8080/v1/users/1
 */
@RestController @RequestMapping("/v1/users")
public class UserV1Controller {
    @GetMapping("/{id}")
    public Map<String,Object> getUser(@PathVariable Long id) {
        return Map.of("id", id, "name", "Alice Johnson", "version", "v1",
            "note", "V1 has combined 'name' field. V2 splits into firstName/lastName.");
    }
    @GetMapping
    public java.util.List<Map<String,Object>> all() {
        return java.util.List.of(Map.of("id",1,"name","Alice Johnson"), Map.of("id",2,"name","Bob Smith"));
    }
}