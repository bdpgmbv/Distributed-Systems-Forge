package com.vyshaliprabananthlal.ecommerce.search;

/**
 * 3/1/26 - 15:50
 *
 * @author Vyshali Prabananth Lal
 */

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SearchServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(SearchServiceApplication.class, args);
        System.out.println("🔎 Search Service is up and listening!");
    }
}
