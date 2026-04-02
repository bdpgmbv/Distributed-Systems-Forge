package com.demo.config;
import com.demo.model.FeatureFlag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.*;

@Slf4j @Service @RequiredArgsConstructor
public class FeatureFlagService {
    private final FeatureFlagRepository repo;

    public boolean isEnabled(String flag, String userId) {
        FeatureFlag f = repo.findById(flag).orElse(null);
        if (f == null || !f.isEnabled()) return false;
        if (f.getAllowedUserIds() != null && !f.getAllowedUserIds().isEmpty())
            return Arrays.asList(f.getAllowedUserIds().split(",")).contains(userId);
        if (f.getRolloutPercentage() < 100) {
            int hash = Math.abs((userId + flag).hashCode() % 100);
            return hash < f.getRolloutPercentage();
        }
        return true;
    }
}
