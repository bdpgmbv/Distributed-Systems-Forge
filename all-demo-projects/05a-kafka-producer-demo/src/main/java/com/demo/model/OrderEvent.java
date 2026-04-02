package com.demo.model;
import lombok.*;
import java.math.BigDecimal;
@Data @NoArgsConstructor @AllArgsConstructor
public class OrderEvent { private String orderId; private String customerId; private BigDecimal amount; private String status; }
