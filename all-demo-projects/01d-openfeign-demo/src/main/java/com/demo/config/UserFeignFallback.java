package com.demo.config;

import com.demo.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;
import java.util.List;

/**
 * Fallback — executes when the target service is DOWN.
 * FallbackFactory gives you the CAUSE (vs plain Fallback which is blind).
 *
 * RULES:
 *   READ operations → return degraded data (log + metric)
 *   WRITE operations → THROW exception (don't silently lose data!)
 */
@Slf4j @Component
public class UserFeignFallback implements FallbackFactory<UserFeignClient> {
    @Override
    public UserFeignClient create(Throwable cause) {
        log.error("⚠️ FALLBACK: {}", cause.getMessage());
        return new UserFeignClient() {
            public User getUser(Long id) { return new User(id, "Unavailable", "N/A"); }
            public List<User> getAllUsers() { return List.of(); }
            public List<User> search(SearchParams p) { return List.of(); }
            public User createUser(User u) { throw new RuntimeException("Service down: " + cause.getMessage()); }
            public void deleteUser(Long id) { throw new RuntimeException("Service down"); }
        };
    }
}
