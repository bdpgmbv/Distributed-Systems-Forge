package com.vyshaliprabananthlal.common;

/**
 * 3/2/26 - 16:52
 *
 * @author Vyshali Prabananth Lal
 */

// The answer from the Fraud service
public record FraudResponse(String transactionId, boolean isFraudulent, String reason) {
}
