package com.demo.model;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
@Entity @Table(name="orders") @Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Order { @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id; private String customerId; private BigDecimal amount; private String status; }
