package com.demo.controller;

import com.demo.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * EMBEDDED USER SERVICE — acts as the "remote" service.
 * In a real microservice architecture, this would be a separate app.
 * Here it's embedded so you can run ONE app and test everything.
 */
@Slf4j
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final Map<Long, User> users = new ConcurrentHashMap<>();
    private final AtomicLong seq = new AtomicLong(0);

    public UserController() {
        save("Alice Johnson", "alice@company.com");
        save("Bob Smith", "bob@company.com");
        save("Charlie Brown", "charlie@company.com");
        save("Diana Prince", "diana@company.com");
        save("Eve Wilson", "eve@company.com");
    }

    @GetMapping("/{id}")
    public User getUser(@PathVariable Long id) {
        log.info("GET /api/users/{}", id);
        User u = users.get(id);
        if (u == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        return u;
    }

    @GetMapping
    public List<User> getAll() {
        log.info("GET /api/users (count={})", users.size());
        return new ArrayList<>(users.values());
    }

    @GetMapping("/search")
    public List<User> search(@RequestParam String name,
                              @RequestParam(defaultValue = "0") int page) {
        log.info("GET /api/users/search?name={}&page={}", name, page);
        return users.values().stream()
            .filter(u -> u.getName().toLowerCase().contains(name.toLowerCase())).toList();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public User create(@RequestBody User u) {
        u.setId(seq.incrementAndGet());
        users.put(u.getId(), u);
        log.info("POST /api/users -> {}", u);
        return u;
    }

    @PutMapping("/{id}")
    public User update(@PathVariable Long id, @RequestBody User u) {
        if (!users.containsKey(id)) throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        u.setId(id);
        users.put(id, u);
        return u;
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) { users.remove(id); }

    @GetMapping("/slow/{id}")
    public User getSlow(@PathVariable Long id,
                         @RequestParam(defaultValue = "5000") long delayMs) throws Exception {
        log.info("GET /api/users/slow/{} — sleeping {}ms", id, delayMs);
        Thread.sleep(delayMs);
        return getUser(id);
    }

    private void save(String name, String email) {
        long id = seq.incrementAndGet();
        users.put(id, new User(id, name, email));
    }
}
