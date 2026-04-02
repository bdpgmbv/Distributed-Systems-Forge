package com.demo.controller;

import com.demo.config.SearchParams;
import com.demo.config.UserFeignClient;
import com.demo.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

/**
 * curl localhost:8080/demo/users/1
 * curl localhost:8080/demo/users
 * curl "localhost:8080/demo/users/search?name=alice"   — @SpringQueryMap demo
 * curl -X POST localhost:8080/demo/users -H "Content-Type: application/json" -d '{"name":"F","email":"f@t.com"}'
 */
@Slf4j @RestController @RequestMapping("/demo") @RequiredArgsConstructor
public class DemoController {
    private final UserFeignClient userClient;

    @GetMapping("/users/{id}")
    public User getUser(@PathVariable Long id) { return userClient.getUser(id); }

    @GetMapping("/users")
    public List<User> getAll() { return userClient.getAllUsers(); }

    @GetMapping("/users/search")
    public Map<String, Object> search(@RequestParam String name,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(required = false) String sort) {
        SearchParams p = new SearchParams(name, page, sort);
        return Map.of("params", p, "results", userClient.search(p),
            "note", "@SpringQueryMap converted POJO → query string automatically");
    }

    @PostMapping("/users")
    public User create(@RequestBody User u) { return userClient.createUser(u); }
}
