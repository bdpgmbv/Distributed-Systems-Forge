package com.demo.controller;
import com.demo.config.UserFeignClient;
import com.demo.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@Slf4j @RestController @RequestMapping("/orders") @RequiredArgsConstructor @Profile("order")
public class OrderController {
    private final UserFeignClient userClient;
    private final DiscoveryClient discovery;

    // curl http://localhost:8082/orders/for-user/1
    @GetMapping("/for-user/{userId}")
    public Map<String,Object> createOrder(@PathVariable Long userId) {
        log.info("Creating order — discovering user-service via Eureka...");
        User user = userClient.getUser(userId);
        log.info("Got user from Eureka: {}", user.getName());
        return Map.of("orderId","ORD-"+System.currentTimeMillis(),"user",user,
            "note","user-service discovered via Eureka! No hardcoded URL.");
    }

    // curl http://localhost:8082/orders/discover
    @GetMapping("/discover")
    public Map<String,Object> discover() {
        Map<String,Object> r = new LinkedHashMap<>();
        r.put("services", discovery.getServices());
        for (String svc : discovery.getServices()) {
            r.put(svc, discovery.getInstances(svc).stream()
                .map(i -> Map.of("host",i.getHost(),"port",i.getPort(),"uri",i.getUri().toString())).toList());
        }
        return r;
    }
}
