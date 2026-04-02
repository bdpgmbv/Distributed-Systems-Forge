package com.demo.controller;
import com.demo.config.FeatureFlagRepository;
import com.demo.config.FeatureFlagService;
import com.demo.model.FeatureFlag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.*;
import java.util.*;

/**
 * curl localhost:8080/flags                              — list all flags
 * curl "localhost:8080/flags/check?flag=new-ui&userId=user-42"  — check for user
 * curl -X POST localhost:8080/flags -H "Content-Type: application/json" \
 *   -d '{"name":"beta-feature","enabled":true,"rolloutPercentage":25}'
 * curl -X PUT "localhost:8080/flags/new-ui/rollout?percent=50"  — change rollout
 * curl "localhost:8080/dashboard?userId=user-42"          — see which dashboard user gets
 */
@Slf4j @RestController @RequiredArgsConstructor
public class DemoController {
    private final FeatureFlagService flagService;
    private final FeatureFlagRepository repo;

    @Bean CommandLineRunner seedFlags(FeatureFlagRepository r) {
        return args -> {
            r.save(FeatureFlag.builder().name("new-ui").enabled(true).rolloutPercentage(20).build());
            r.save(FeatureFlag.builder().name("dark-mode").enabled(true).rolloutPercentage(100).build());
            r.save(FeatureFlag.builder().name("beta-search").enabled(true).allowedUserIds("user-1,user-5,user-99").build());
            r.save(FeatureFlag.builder().name("kill-switch").enabled(false).rolloutPercentage(100).build());
            log.info("Seeded 4 feature flags");
        };
    }

    @GetMapping("/flags")
    public List<FeatureFlag> listFlags() { return repo.findAll(); }

    @GetMapping("/flags/check")
    public Map<String,Object> checkFlag(@RequestParam String flag, @RequestParam String userId) {
        boolean enabled = flagService.isEnabled(flag, userId);
        return Map.of("flag", flag, "userId", userId, "enabled", enabled,
            "hash", Math.abs((userId+flag).hashCode()%100));
    }

    @PostMapping("/flags")
    public FeatureFlag createFlag(@RequestBody FeatureFlag f) { return repo.save(f); }

    @PutMapping("/flags/{name}/rollout")
    public FeatureFlag updateRollout(@PathVariable String name, @RequestParam int percent) {
        FeatureFlag f = repo.findById(name).orElseThrow();
        f.setRolloutPercentage(percent);
        return repo.save(f);
    }

    @PutMapping("/flags/{name}/toggle")
    public FeatureFlag toggle(@PathVariable String name) {
        FeatureFlag f = repo.findById(name).orElseThrow();
        f.setEnabled(!f.isEnabled());
        return repo.save(f);
    }

    @GetMapping("/dashboard")
    public Map<String,Object> dashboard(@RequestParam String userId) {
        boolean newUi = flagService.isEnabled("new-ui", userId);
        boolean darkMode = flagService.isEnabled("dark-mode", userId);
        return Map.of("userId", userId, "dashboard", newUi ? "NEW_DASHBOARD_V2" : "LEGACY_DASHBOARD",
            "darkMode", darkMode, "note", newUi ? "You're in the 20% rollout!" : "You're on the old dashboard");
    }
}
