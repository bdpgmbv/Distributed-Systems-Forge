package com.demo.config;

import com.demo.model.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * OpenFeign Client — just an interface. Spring generates the HTTP client.
 * When you call userClient.getUser(42):
 *   1. Feign reads @GetMapping → GET request
 *   2. Reads @PathVariable → replaces {id} with 42
 *   3. Sends: GET http://localhost:8080/api/users/42
 *   4. Deserializes JSON → User
 */
@FeignClient(name = "user-service", url = "http://localhost:8080",
    fallbackFactory = UserFeignFallback.class)
public interface UserFeignClient {
    @GetMapping("/api/users/{id}")
    User getUser(@PathVariable("id") Long id);

    @GetMapping("/api/users")
    List<User> getAllUsers();

    @GetMapping("/api/users/search")
    List<User> search(@SpringQueryMap SearchParams params);

    @PostMapping("/api/users")
    User createUser(@RequestBody User user);

    @DeleteMapping("/api/users/{id}")
    void deleteUser(@PathVariable("id") Long id);
}
