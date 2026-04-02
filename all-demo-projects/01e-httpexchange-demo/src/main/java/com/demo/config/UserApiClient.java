package com.demo.config;

import com.demo.model.User;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.*;
import java.util.List;

/**
 * @HttpExchange — Spring 6.1 NATIVE declarative client.
 * Like Feign but built into Spring Framework. No spring-cloud dependency.
 * Spring generates the implementation from this interface.
 */
@HttpExchange("/api/users")
public interface UserApiClient {
    @GetExchange("/{id}")
    User getUser(@PathVariable Long id);

    @GetExchange
    List<User> getAllUsers();

    @PostExchange
    User createUser(@RequestBody User user);

    @PutExchange("/{id}")
    User updateUser(@PathVariable Long id, @RequestBody User user);

    @DeleteExchange("/{id}")
    void deleteUser(@PathVariable Long id);
}
