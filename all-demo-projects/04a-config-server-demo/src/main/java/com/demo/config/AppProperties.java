package com.demo.config;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

@Component @ConfigurationProperties(prefix = "app") @RefreshScope @Data
public class AppProperties {
    private String greeting;
    private int maxItems;
    private Feature feature = new Feature();
    @Data
    public static class Feature { private boolean newDashboard; private boolean darkMode; }
}
