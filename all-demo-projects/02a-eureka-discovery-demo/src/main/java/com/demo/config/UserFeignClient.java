package com.demo.config;
import com.demo.model.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
// Eureka resolves "user-service" → actual IP:port. No hardcoded URL!
@FeignClient("user-service")
public interface UserFeignClient {
    @GetMapping("/api/users/{id}") User getUser(@PathVariable("id") Long id);
}
