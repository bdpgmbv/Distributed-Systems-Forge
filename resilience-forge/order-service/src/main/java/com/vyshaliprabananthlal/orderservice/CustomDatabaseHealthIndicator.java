package com.vyshaliprabananthlal.orderservice;

/**
 * 3/2/26 - 10:13
 *
 * @author Vyshali Prabananth Lal
 */

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class CustomDatabaseHealthIndicator implements HealthIndicator {

    private final JdbcTemplate jdbcTemplate;

    public CustomDatabaseHealthIndicator(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Health health() {
        try {
            // We execute a tiny, fast query to prove the DB can actually process data
            jdbcTemplate.queryForObject("SELECT 1", Integer.class);

            return Health.up().withDetail("Database Status", "Responding perfectly to queries!").withDetail("Ping Tool", "Raw JDBC SELECT 1").build();

        } catch (Exception e) {
            return Health.down().withDetail("Database Status", "Unresponsive or connection lost!").withException(e).build();
        }
    }
}