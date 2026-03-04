package com.vyshaliprabananthlal.fraud.repository;

/**
 * 3/2/26 - 17:14
 *
 * @author Vyshali Prabananth Lal
 */

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class FraudRepository {

    private final JdbcTemplate jdbcTemplate;

    public FraudRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public String checkAccountStatus(String accountId) {
        String sql = "SELECT reason FROM blocked_accounts WHERE account_id = ?";
        try {
            // Query the DB. If it finds a reason, it returns it.
            return jdbcTemplate.queryForObject(sql, String.class, accountId);
        } catch (org.springframework.dao.EmptyResultDataAccessException e) {
            // If the query finds 0 rows, the account isn't blocked. Safe!
            return null;
        }
    }
}
