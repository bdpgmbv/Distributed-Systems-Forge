package com.demo.model;
import jakarta.persistence.*;
import lombok.*;

@Entity @Table(name="feature_flags") @Data @NoArgsConstructor @AllArgsConstructor @Builder
public class FeatureFlag {
    @Id private String name;
    private boolean enabled;
    private int rolloutPercentage;
    private String allowedUserIds;
}
