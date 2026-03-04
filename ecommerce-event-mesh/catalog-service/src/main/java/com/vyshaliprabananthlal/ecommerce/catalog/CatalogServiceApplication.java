package com.vyshaliprabananthlal.ecommerce.catalog;

/**
 * 3/1/26 - 15:39
 *
 * @author Vyshali Prabananth Lal
 */

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling // We are adding this now so our Outbox Poller can run automatically!
public class CatalogServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(CatalogServiceApplication.class, args);
        System.out.println("🚀 Catalog Service is up and running!");
    }
}
