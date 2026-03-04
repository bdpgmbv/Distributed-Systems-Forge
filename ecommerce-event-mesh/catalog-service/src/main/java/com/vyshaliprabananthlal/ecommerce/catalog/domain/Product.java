package com.vyshaliprabananthlal.ecommerce.catalog.domain;

/**
 * 3/1/26 - 15:34
 *
 * @author Vyshali Prabananth Lal
 */

import java.math.BigDecimal;

// A Record is a simple, immutable data carrier in modern Java.
public record Product(String id, String name, BigDecimal price) {
}
