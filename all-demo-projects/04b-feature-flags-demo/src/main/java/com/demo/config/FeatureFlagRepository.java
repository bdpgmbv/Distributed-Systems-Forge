package com.demo.config;
import com.demo.model.FeatureFlag;
import org.springframework.data.jpa.repository.JpaRepository;
public interface FeatureFlagRepository extends JpaRepository<FeatureFlag, String> {}
