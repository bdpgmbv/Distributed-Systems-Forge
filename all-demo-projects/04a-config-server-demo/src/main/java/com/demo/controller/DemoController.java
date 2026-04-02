package com.demo.controller;
import com.demo.config.AppProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

/**
 * curl localhost:8080/demo/config     — see current config values
 * curl localhost:8080/demo/features   — see feature flags
 *
 * To refresh config without restart:
 *   1. Edit config-repo/demo-service.yml
 *   2. POST localhost:8080/actuator/refresh
 *   3. curl localhost:8080/demo/config — values updated!
 */
@Slf4j @RestController @RequestMapping("/demo") @RequiredArgsConstructor @Profile("client")
public class DemoController {
    private final AppProperties props;

    @GetMapping("/config")
    public Map<String,Object> getConfig() {
        return Map.of("greeting", props.getGreeting(), "maxItems", props.getMaxItems(),
            "note", "These values come from Config Server. Edit config-repo/*.yml and POST /actuator/refresh to update live!");
    }

    @GetMapping("/features")
    public Map<String,Object> getFeatures() {
        return Map.of("newDashboard", props.getFeature().isNewDashboard(),
            "darkMode", props.getFeature().isDarkMode(),
            "note", "Toggle in config-repo/demo-service.yml → POST /actuator/refresh → instant update!");
    }
}
