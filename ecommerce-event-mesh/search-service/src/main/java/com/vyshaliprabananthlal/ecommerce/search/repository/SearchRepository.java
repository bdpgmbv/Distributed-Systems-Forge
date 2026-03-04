package com.vyshaliprabananthlal.ecommerce.search.repository;

/**
 * 3/1/26 - 15:52
 *
 * @author Vyshali Prabananth Lal
 */

import com.vyshaliprabananthlal.ecommerce.search.domain.ProductEventDto;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class SearchRepository {

    private final JdbcTemplate jdbcTemplate;

    public SearchRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void upsertProductView(ProductEventDto product) {
        // "ON CONFLICT" makes this operation Idempotent!
        String sql = """
                INSERT INTO search_read_model (id, name, price) 
                VALUES (?, ?, ?) 
                ON CONFLICT (id) 
                DO UPDATE SET name = EXCLUDED.name, price = EXCLUDED.price, last_updated = CURRENT_TIMESTAMP
                """;

        jdbcTemplate.update(sql, product.id(), product.name(), product.price());
    }

    // Add this to your existing SearchRepository class
    public java.util.List<ProductEventDto> getAllProducts() {
        String sql = "SELECT id, name, price FROM search_read_model ORDER BY last_updated DESC";

        return jdbcTemplate.query(sql, (rs, rowNum) -> new ProductEventDto(
                rs.getString("id"),
                rs.getString("name"),
                rs.getBigDecimal("price")
        ));
    }
}