package com.retail.reward.model;
import java.util.Map;

/**
 * Data Transfer Object representing the calculated rewards for a customer.
 * * @param customerId The unique identifier of the customer.
 * @param monthlyPoints A map of dynamically resolved month names to their accumulated points.
 * @param totalPoints The total accumulated points across all months.
 */
public record RewardSummary(Long customerId, Map<String, Integer> monthlyPoints, int totalPoints) {
}
