package com.demo.model;
import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
@Entity @Table(name="outbox_events") @Data @NoArgsConstructor @AllArgsConstructor @Builder
public class OutboxEvent { @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id; private String aggregateType; private String aggregateId; private String eventType; @Column(columnDefinition="TEXT") private String payload; private boolean published; private Instant createdAt; }
