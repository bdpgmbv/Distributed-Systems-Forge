package com.vyshaliprabananthlal.ecommerce.payment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 3/1/26 - 21:42
 *
 * @author Vyshali Prabananth Lal
 */

@SpringBootApplication
public class PaymentServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(PaymentServiceApplication.class, args);
        System.out.println("🛒 Order Service (Saga Orchestrator) is running!");
    }
}
