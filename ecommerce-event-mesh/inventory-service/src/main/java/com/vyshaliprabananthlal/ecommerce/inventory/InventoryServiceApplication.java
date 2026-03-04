package com.vyshaliprabananthlal.ecommerce.inventory;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 3/1/26 - 21:43
 *
 * @author Vyshali Prabananth Lal
 */

@SpringBootApplication
public class InventoryServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(InventoryServiceApplication.class, args);
        System.out.println("🛒 Order Service (Saga Orchestrator) is running!");
    }
}
