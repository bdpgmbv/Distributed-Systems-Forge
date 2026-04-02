package com.demo.model;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
@Entity @Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Product { @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id; private String name; private BigDecimal price; private String category; }
