package com.demo.controller;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import java.net.InetAddress;
import java.util.Map;

@Slf4j @RestController
public class DemoController {
    @Value("${spring.application.name}") private String appName;

    @GetMapping("/")
    public Map<String,Object> info() throws Exception {
        return Map.of("app", appName, "hostname", InetAddress.getLocalHost().getHostName(),
            "javaVersion", System.getProperty("java.version"),
            "maxMemory", Runtime.getRuntime().maxMemory() / 1024 / 1024 + "MB",
            "availableProcessors", Runtime.getRuntime().availableProcessors(),
            "note", "In K8s, hostname = pod name. Each replica has a different hostname.");
    }

    @GetMapping("/heavy")
    public Map<String,Object> heavy() throws Exception {
        byte[] mem = new byte[50 * 1024 * 1024]; // 50MB allocation
        Thread.sleep(2000);
        return Map.of("allocated", "50MB", "note", "Use this to trigger HPA scaling based on memory/CPU");
    }
}