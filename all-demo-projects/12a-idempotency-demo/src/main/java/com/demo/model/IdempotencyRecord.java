package com.demo.model;
import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
@Entity @Table(name="idempotency_keys") @Data @NoArgsConstructor @AllArgsConstructor @Builder
public class IdempotencyRecord {
    @Id private String idempotencyKey;
    @Column(columnDefinition="TEXT") private String responseBody;
    private int responseStatus;
    private Instant createdAt;
}