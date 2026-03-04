package com.vyshaliprabananthlal.common;

/**
 * 3/2/26 - 16:52
 *
 * @author Vyshali Prabananth Lal
 */

// A simple record containing the transaction details we want to verify
public record FraudRequest(String transactionId, String accountId, Double amount) {
}
