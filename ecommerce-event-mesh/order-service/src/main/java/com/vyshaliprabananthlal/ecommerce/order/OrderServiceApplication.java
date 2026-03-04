package com.vyshaliprabananthlal.ecommerce.order;

/**
 * 3/1/26 - 21:09
 *
 * @author Vyshali Prabananth Lal
 */

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class OrderServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(OrderServiceApplication.class, args);
        System.out.println("🛒 Order Service (Saga Orchestrator) is running!");
    }
}
