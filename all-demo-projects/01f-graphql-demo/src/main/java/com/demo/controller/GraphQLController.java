package com.demo.controller;

import com.demo.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.*;
import org.springframework.stereotype.Controller;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * GraphQL Controller — Query + Mutation.
 *
 * TEST IN BROWSER: http://localhost:8080/graphiql
 *
 * Queries to try:
 *   { user(id: 1) { name email } }
 *   { users { id name } }
 *   { searchUsers(name: "alice") { name email } }
 *
 * Mutation:
 *   mutation { createUser(name: "GQL", email: "gql@t.com") { id name } }
 *   mutation { deleteUser(id: 1) }
 *
 * curl:
 *   curl -X POST localhost:8080/graphql -H "Content-Type: application/json" \
 *     -d '{"query":"{ user(id:1) { name email } }"}'
 */
@Slf4j
@Controller
public class GraphQLController {

    private final Map<Long, User> users = new ConcurrentHashMap<>();
    private final AtomicLong seq = new AtomicLong(0);

    public GraphQLController() {
        add("Alice Johnson", "alice@company.com");
        add("Bob Smith", "bob@company.com");
        add("Charlie Brown", "charlie@company.com");
    }

    @QueryMapping
    public User user(@Argument Long id) {
        log.info("GraphQL: user(id={})", id);
        return users.get(id);
    }

    @QueryMapping
    public List<User> users() {
        log.info("GraphQL: users()");
        return new ArrayList<>(users.values());
    }

    @QueryMapping
    public List<User> searchUsers(@Argument String name) {
        log.info("GraphQL: searchUsers(name={})", name);
        return users.values().stream()
            .filter(u -> u.getName().toLowerCase().contains(name.toLowerCase())).toList();
    }

    @MutationMapping
    public User createUser(@Argument String name, @Argument String email) {
        log.info("GraphQL: createUser({}, {})", name, email);
        long id = seq.incrementAndGet();
        User u = new User(id, name, email);
        users.put(id, u);
        return u;
    }

    @MutationMapping
    public boolean deleteUser(@Argument Long id) {
        log.info("GraphQL: deleteUser({})", id);
        return users.remove(id) != null;
    }

    private void add(String n, String e) { long id = seq.incrementAndGet(); users.put(id, new User(id, n, e)); }
}
