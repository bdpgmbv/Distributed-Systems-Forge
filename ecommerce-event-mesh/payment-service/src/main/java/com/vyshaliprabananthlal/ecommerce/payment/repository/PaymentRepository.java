package com.vyshaliprabananthlal.ecommerce.payment.repository;

/**
 * 3/1/26 - 21:12
 *
 * @author Vyshali Prabananth Lal
 */

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class PaymentRepository {
    private final JdbcTemplate jdbcTemplate;

    public PaymentRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * This uses Postgres ON CONFLICT to atomically check and insert.
     * Returns TRUE if this is a brand new command.
     * Returns FALSE if we have already seen this command before.
     */
    public boolean checkAndSaveIdempotencyKey(String commandId) {
        String sql = "INSERT INTO processed_commands (command_id) VALUES (?) ON CONFLICT (command_id) DO NOTHING";
        int rowsAffected = jdbcTemplate.update(sql, commandId);
        return rowsAffected > 0;
    }
}
