package com.vyshaliprabananthlal.orderservice;

/**
 * 3/2/26 - 09:43
 *
 * @author Vyshali Prabananth Lal
 */

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class OrderRepository {

    private final JdbcTemplate jdbcTemplate;

    public OrderRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // Maps the database row to our Java Record
    private final RowMapper<Order> rowMapper = (rs, rowNum) -> new Order(rs.getLong("id"), rs.getString("item_name"), rs.getString("status"));

    public void save(String itemName) {
        String sql = "INSERT INTO orders (item_name, status) VALUES (?, 'CREATED')";
        jdbcTemplate.update(sql, itemName);
    }

    public List<Order> findAll() {
        String sql = "SELECT * FROM orders";
        return jdbcTemplate.query(sql, rowMapper);
    }
}
