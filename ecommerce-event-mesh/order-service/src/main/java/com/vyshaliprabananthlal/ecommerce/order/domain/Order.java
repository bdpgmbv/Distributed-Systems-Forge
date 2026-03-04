package com.vyshaliprabananthlal.ecommerce.order.domain;

/**
 * 3/1/26 - 21:03
 *
 * @author Vyshali Prabananth Lal
 */

import java.math.BigDecimal;

public record Order(String id, String productId, BigDecimal amount, String status) {
}
