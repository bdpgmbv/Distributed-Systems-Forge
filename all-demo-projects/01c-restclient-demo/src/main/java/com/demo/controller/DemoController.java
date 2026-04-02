package com.demo.controller;

import com.demo.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

/**
 * RestClient Demo — Spring Boot 3.2+ modern synchronous client.
 * The OFFICIAL RestTemplate replacement.
 *
 * curl localhost:8080/demo/users/1           — basic GET
 * curl localhost:8080/demo/users             — GET list
 * curl localhost:8080/demo/users/1/full      — full ResponseEntity
 * curl localhost:8080/demo/users/999/safe    — exchange() error handling
 */
@Slf4j @RestController @RequestMapping("/demo") @RequiredArgsConstructor
public class DemoController {
    private final RestClient restClient;

    @GetMapping("/users/{id}")
    public User getUser(@PathVariable Long id) {
        return restClient.get().uri("/api/users/{id}", id).retrieve().body(User.class);
    }

    @GetMapping("/users")
    public List<User> getAll() {
        return restClient.get().uri("/api/users").retrieve()
            .body(new ParameterizedTypeReference<>() {});
    }

    @PostMapping("/users")
    public User create(@RequestBody User u) {
        return restClient.post().uri("/api/users").body(u).retrieve().body(User.class);
    }

    /** Full ResponseEntity — access status + headers + body */
    @GetMapping("/users/{id}/full")
    public Map<String, Object> getFull(@PathVariable Long id) {
        ResponseEntity<User> e = restClient.get().uri("/api/users/{id}", id)
            .retrieve().toEntity(User.class);
        return Map.of("status", e.getStatusCode().value(),
            "content_type", String.valueOf(e.getHeaders().getContentType()),
            "body", e.getBody());
    }

    /** exchange() — full control: handle every status code yourself */
    @GetMapping("/users/{id}/safe")
    public Map<String, Object> getSafe(@PathVariable Long id) {
        return restClient.get().uri("/api/users/{id}", id)
            .exchange((req, resp) -> {
                if (resp.getStatusCode().is2xxSuccessful())
                    return Map.<String, Object>of("status", "FOUND", "user", resp.bodyTo(User.class));
                if (resp.getStatusCode().value() == 404)
                    return Map.<String, Object>of("status", "NOT_FOUND", "message", "User " + id + " doesn't exist");
                return Map.<String, Object>of("status", "ERROR", "code", resp.getStatusCode().value());
            });
    }
}
