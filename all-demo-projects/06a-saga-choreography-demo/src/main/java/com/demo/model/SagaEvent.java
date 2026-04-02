package com.demo.model;
import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
@Entity @Table(name="saga_events") @Data @NoArgsConstructor @AllArgsConstructor @Builder
public class SagaEvent {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    private Long orderId;
    private String eventType;
    private String serviceName;
    private String details;
    private Instant timestamp;
}
