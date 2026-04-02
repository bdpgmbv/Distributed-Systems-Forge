package com.demo.controller;
import com.demo.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.*;
@RestController @RequiredArgsConstructor
public class AuthController {
    private final JwtService jwt;
    @PostMapping("/auth/login")
    public Map<String,Object> login(@RequestParam String username, @RequestParam(defaultValue="USER") List<String> roles) {
        String token=jwt.generate(username,roles);
        return Map.of("token",token,"usage","curl localhost:8080/api/profile -H 'Authorization: Bearer TOKEN'");
    }
    @GetMapping("/api/profile")
    public Map<String,Object> profile(@AuthenticationPrincipal String u) { return Map.of("user",u,"auth","JWT verified!"); }
    @GetMapping("/api/admin") @PreAuthorize("hasRole('ADMIN')")
    public Map<String,Object> admin(@AuthenticationPrincipal String u) { return Map.of("user",u,"access","ADMIN only!"); }
}