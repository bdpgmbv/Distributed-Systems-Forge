package com.demo.controller;

import com.demo.config.UserApiClient;
import com.demo.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * curl localhost:8080/demo/users/1
 * curl localhost:8080/demo/users
 * curl -X POST localhost:8080/demo/users -H "Content-Type: application/json" -d '{"name":"HE","email":"he@t.com"}'
 */
@RestController @RequestMapping("/demo") @RequiredArgsConstructor
public class DemoController {
    private final UserApiClient client;

    @GetMapping("/users/{id}")
    public User get(@PathVariable Long id) { return client.getUser(id); }

    @GetMapping("/users")
    public List<User> getAll() { return client.getAllUsers(); }

    @PostMapping("/users")
    public User create(@RequestBody User u) { return client.createUser(u); }
}
