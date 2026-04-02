package com.demo.controller;

import com.demo.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.*;

/**
 * RestTemplate Demo — every operation a senior architect should know.
 *
 * Run: ./gradlew bootRun
 * Test: See curl commands below each method.
 */
@Slf4j
@RestController
@RequestMapping("/demo")
@RequiredArgsConstructor
public class DemoController {

    private final RestTemplate restTemplate;

    /**
     * GET single user — simplest form.
     * curl http://localhost:8080/demo/users/1
     */
    @GetMapping("/users/{id}")
    public User getUser(@PathVariable Long id) {
        return restTemplate.getForObject("/api/users/{id}", User.class, id);
    }

    /**
     * GET list — MUST use ParameterizedTypeReference for generics!
     * Without it: returns List<LinkedHashMap> — SILENT BUG.
     * curl http://localhost:8080/demo/users
     */
    @GetMapping("/users")
    public List<User> getAllUsers() {
        return restTemplate.exchange("/api/users", HttpMethod.GET, null,
            new ParameterizedTypeReference<List<User>>() {}).getBody();
    }

    /**
     * POST — create a user.
     * curl -X POST http://localhost:8080/demo/users \
     *   -H "Content-Type: application/json" \
     *   -d '{"name":"NewUser","email":"new@test.com"}'
     */
    @PostMapping("/users")
    public User createUser(@RequestBody User user) {
        return restTemplate.postForObject("/api/users", user, User.class);
    }

    /**
     * PUT — update a user (returns void, so we use exchange for response).
     * curl -X PUT http://localhost:8080/demo/users/1 \
     *   -H "Content-Type: application/json" \
     *   -d '{"name":"Updated","email":"updated@test.com"}'
     */
    @PutMapping("/users/{id}")
    public User updateUser(@PathVariable Long id, @RequestBody User user) {
        return restTemplate.exchange("/api/users/{id}", HttpMethod.PUT,
            new HttpEntity<>(user), User.class, id).getBody();
    }

    /**
     * SEARCH — dynamic URL with optional query params.
     * UriComponentsBuilder is the SAFE way (vs string concatenation).
     * curl "http://localhost:8080/demo/users/search?name=alice&page=0"
     */
    @GetMapping("/users/search")
    public List<User> search(@RequestParam String name,
                              @RequestParam(defaultValue = "0") int page,
                              @RequestParam(required = false) String sort) {
        URI uri = UriComponentsBuilder.fromPath("/api/users/search")
            .queryParam("name", name)
            .queryParam("page", page)
            .queryParamIfPresent("sort", Optional.ofNullable(sort))
            .build().toUri();
        return restTemplate.exchange(uri, HttpMethod.GET, null,
            new ParameterizedTypeReference<List<User>>() {}).getBody();
    }

    /**
     * CUSTOM HEADERS — adding auth, correlation ID.
     * curl http://localhost:8080/demo/users/1/secure
     */
    @GetMapping("/users/{id}/secure")
    public Map<String, Object> getUserSecure(@PathVariable Long id) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth("my-jwt-token-12345");
        headers.set("X-Custom-Header", "architect-demo");
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<User> resp = restTemplate.exchange(
            "/api/users/{id}", HttpMethod.GET, entity, User.class, id);

        return Map.of("user", resp.getBody(),
            "status", resp.getStatusCode().value(),
            "note", "Check user-service logs — you'll see the Authorization header arrive");
    }

    /**
     * TIMEOUT TEST — calls slow endpoint.
     * curl http://localhost:8080/demo/timeout/fast    (2s delay, succeeds)
     * curl http://localhost:8080/demo/timeout/slow    (10s delay, TIMES OUT at 5s!)
     */
    @GetMapping("/timeout/{speed}")
    public Map<String, Object> timeoutTest(@PathVariable String speed) {
        long delay = speed.equals("slow") ? 10000 : 2000;
        long start = System.currentTimeMillis();
        try {
            User user = restTemplate.getForObject(
                "/api/users/slow/1?delayMs=" + delay, User.class);
            return Map.of("status", "SUCCESS", "user", user,
                "elapsed_ms", System.currentTimeMillis() - start);
        } catch (ResourceAccessException e) {
            return Map.of("status", "TIMEOUT", "error", e.getMessage(),
                "elapsed_ms", System.currentTimeMillis() - start,
                "lesson", "Read timeout (5s) kicked in. Without it, thread waits FOREVER.");
        }
    }

    /**
     * ERROR HANDLING — demonstrates catching HTTP errors.
     * curl http://localhost:8080/demo/users/999/handled
     */
    @GetMapping("/users/{id}/handled")
    public Map<String, Object> getUserHandled(@PathVariable Long id) {
        try {
            User user = restTemplate.getForObject("/api/users/{id}", User.class, id);
            return Map.of("status", "FOUND", "user", user);
        } catch (HttpClientErrorException.NotFound e) {
            return Map.of("status", "NOT_FOUND", "message", "User " + id + " doesn't exist",
                "http_status", 404, "note", "Caught HttpClientErrorException.NotFound");
        } catch (HttpClientErrorException e) {
            return Map.of("status", "CLIENT_ERROR", "http_status", e.getStatusCode().value());
        } catch (ResourceAccessException e) {
            return Map.of("status", "SERVICE_DOWN", "error", e.getMessage());
        }
    }
}
