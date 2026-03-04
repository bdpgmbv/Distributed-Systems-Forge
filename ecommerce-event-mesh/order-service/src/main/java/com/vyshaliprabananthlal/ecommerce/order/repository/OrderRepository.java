package com.vyshaliprabananthlal.ecommerce.order.repository;

/**
 * 3/1/26 - 21:04
 *
 * @author Vyshali Prabananth Lal
 */

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class OrderRepository {
    private final JdbcTemplate jdbcTemplate;

    public OrderRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void createOrder(String id, String productId, java.math.BigDecimal amount, String status) {
        String sql = "INSERT INTO orders (id, product_id, amount, status) VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(sql, id, productId, amount, status);
    }

    public void updateStatus(String id, String newStatus) {
        String sql = "UPDATE orders SET status = ? WHERE id = ?";
        jdbcTemplate.update(sql, newStatus, id);
        System.out.println("🔄 Order " + id + " state transitioned to: " + newStatus);
    }
}
